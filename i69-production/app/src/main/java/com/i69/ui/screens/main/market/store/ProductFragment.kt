package com.i69.ui.screens.main.market.store

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.market.FilterColorSkus
import com.i69.data.models.market.FilterSizeSkus
import com.i69.data.models.market.FreightEstimateResponse
import com.i69.data.models.market.Products
import com.i69.data.models.market.SkuInfo
import com.i69.databinding.FragmentProductBinding
import com.i69.ui.adapters.FreightEstimateAdapter
import com.i69.ui.adapters.SkuColorAdapter
import com.i69.ui.adapters.SkuListAdapter
import com.i69.ui.adapters.SkuSizeAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.viewModels.MarketPlacesViewModel
import com.i69.utils.loadImage
import com.i69.utils.navigate
import com.paypal.pyplcheckout.common.extensions.activity
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@Keep
data class Photo(
    val id: String,
    var url: String?,
    var type: String,
    var resellPrice: String,
    var skuPropertyId: String
)

class ProductFragment : BaseFragment<FragmentProductBinding>(),
    SkuColorAdapter.ClickColorItemListener, SkuSizeAdapter.ClickSizeItemListener,
    SkuColorAdapter.ShowImageOnViewPager, FreightEstimateAdapter.SelectSingleFreightEstimate {

    private val marketPlacesViewModel: MarketPlacesViewModel by activityViewModels()
    lateinit var adapter: ImageSliderAdapter
    private lateinit var product: Products
    var TAG: String = ProductFragment::class.java.simpleName
    private lateinit var freightEstimateAdapter: FreightEstimateAdapter
    private lateinit var skuColorAdapter: SkuColorAdapter
    private lateinit var skuSizeAdapter: SkuSizeAdapter
    private val filterColorSkus: MutableList<FilterColorSkus> = mutableListOf()
    private var quantity: Int = 1
    private var availableQuantity: Int = 0
    private val photoList = arrayListOf<Photo>()
    private var productId1 = ""
    private var skuId = ""
    private var userId = ""
    private var userToken = ""
    private var freightEstimateResponse = mutableListOf<FreightEstimateResponse>()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProductBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
        }
        binding?.layContainer?.viewTreeObserver?.addOnGlobalLayoutListener {
            val screenDisplay = activity?.resources?.displayMetrics
            val layoutParams = binding?.layContainer?.layoutParams
            val windowWidth = screenDisplay?.widthPixels ?: 0
            layoutParams?.width = windowWidth
            layoutParams?.height = windowWidth
            binding?.layContainer?.layoutParams = layoutParams
        }

        binding?.recyclerColor?.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        binding?.recyclerSizes?.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        skuColorAdapter = SkuColorAdapter(activity, filterColorSkus, this, this)
        binding?.recyclerColor?.adapter = skuColorAdapter

        marketPlacesViewModel.marketPlacesProduct.observe(viewLifecycleOwner) {
            binding?.txtProductName!!.text = "" + (it.itemInfo?.subject)
            setHtmlWithImages(binding?.descriptionTV!!, it.itemInfo?.detail!!)
            binding?.txtDiscountedPrice!!.text =
                it.skuInfo[0].currencySymbol + "" + it.skuInfo[0].resellPrice
            binding?.originalPrice!!.text =
                it.skuInfo[0].currencySymbol + "" + it.skuInfo[0].markupPrice
            var parts = it.skuInfo[0].skuAttr?.split("#")
            binding?.txtSkuName!!.text = parts?.last()
            binding?.txtRatings!!.text = it.itemInfo?.avgEvaluationRating
            binding?.ratingBar?.rating = it.itemInfo?.avgEvaluationRating!!.toFloat()
            binding?.txtSoldNumbers?.text = it.itemInfo?.salesCount + " sold"
            binding?.offTV!!.text = it.skuInfo[0].discountPercentage + "\noff"
            binding?.storeTV!!.text = it.storeInfo?.storeName
            productId1 = it.itemInfo!!.productId ?: ""

            photoList.clear()
            it.multimediaInfo?.imageUrls?.forEachIndexed { index, url ->
                val photo = Photo(
                    id = index.toString(),
                    url = url,
                    type = "image", "", ""
                )
                photoList.add(photo)
            }

            filterColorSkus.clear()
            val groupedColors = mutableMapOf<String, ColorInfo>()
            val skuInfo1 = it.skuInfo
            for (skuInfo in skuInfo1) {
                for (skuProperty in skuInfo.aeSkuPropertyDtos) {
                    if (skuProperty.skuPropertyName == "Color") {
                        val color = skuProperty.skuPropertyValue ?: continue
                        val colorId = skuProperty.skuPropertyId ?: ""
                        val colorImage = skuProperty.skuImage ?: ""
                        val propertyValueId = skuProperty.propertyValueId ?: ""
                        val currencySymbol = skuInfo.currencySymbol ?: ""
                        val resellPrice = skuInfo.resellPrice ?: ""
                        val availableStock = skuInfo.availableStock ?: ""
                        val skuId = skuInfo.skuId ?: ""

                        val photo = Photo(
                            id = (photoList.size - 1).toString(),
                            url = colorImage,
                            type = "image", currencySymbol + "" + resellPrice, propertyValueId
                        )
                        photoList.add(photo)
                        groupedColors.putIfAbsent(
                            color,
                            ColorInfo(
                                propertyValueId,
                                colorImage,
                                resellPrice,
                                currencySymbol,
                                availableStock,
                                skuId,
                                mutableListOf()
                            )
                        )
                    }

//                        if (skuProperty.skuPropertyName == "Size") {
                    if (skuProperty.skuPropertyName != "Color") {
                        val size = skuProperty.skuPropertyValue ?: continue
                        val sizeId = skuProperty.skuPropertyId ?: ""
                        val sizeImage = skuProperty.skuImage ?: ""
                        val skuPropertyName = skuProperty.skuPropertyName ?: ""

                        val relatedColor = skuInfo.aeSkuPropertyDtos
                            .takeWhile { it != skuProperty }
                            .lastOrNull { it.skuPropertyName == "Color" }
                            ?.skuPropertyValue

                        if (relatedColor != null) {
                            groupedColors[relatedColor]?.items?.add(
                                FilterSizeSkus(
                                    skuPropertyName = skuPropertyName,
                                    size = size,
                                    skuImage = sizeImage,
                                    skuPropertyId = sizeId
                                )
                            )
                        }
                    }
                }
            }
            val groupedColorsEntries = groupedColors.entries.toList()
            if (groupedColorsEntries.isNotEmpty()) {
                groupedColorsEntries.forEachIndexed { index, (color, colorInfo) ->
                    filterColorSkus.add(
                        FilterColorSkus(
                            color = color,
                            skuPropertyId = colorInfo.colorId,
                            skuImage = colorInfo.colorImage,
                            resellPrice = colorInfo.resellPrice,
                            currencySymbol = colorInfo.currencySymbol,
                            availableStock = colorInfo.availableStock,
                            skuId = colorInfo.skuId,
                            filterSizeSkus = colorInfo.items.distinctBy { it.size }
                        )
                    )
                    if (index == groupedColorsEntries.size - 1) {
                        adapter = fragmentManager?.let {
                            ImageSliderAdapter(
                                requireActivity(),
                                photoList
                            )
                        }!!
                        binding?.container?.adapter = adapter
                    }
                }
            } else {
                adapter = fragmentManager?.let {
                    ImageSliderAdapter(
                        requireActivity(),
                        photoList
                    )
                }!!
                binding?.container?.adapter = adapter
            }

            skuColorAdapter.addColorItems(filterColorSkus)
            binding?.recyclerColor?.adapter = skuColorAdapter

            binding?.imgLeft?.setOnClickListener {
                if (binding?.container?.currentItem!! > 0) {
                    binding?.container?.setCurrentItem(binding?.container?.currentItem!! - 1)
                }
            }

            binding?.imgRight?.setOnClickListener {
                if (binding?.container?.currentItem!! < photoList.size - 1)
                    binding?.container?.setCurrentItem(binding?.container?.currentItem!! + 1)
            }

            val productInfo = it

            val skuInfoList = mutableListOf<SkuInfo>()
            it.skuInfo.forEach {
                skuInfoList.add(
                    SkuInfo(
                        it.skuAttr,
                        it.skuPrice,
                        it.markupPrice,
                        it.discountedPrice,
                        it.discountPercentage,
                        it.currency,
                        it.currencySymbol,
                        it.availableStock,
                        it.completeDetails
                    )
                )
            }

            if (product.productId.equals(it.itemInfo!!.productId))
                hideProgressView()

            binding?.laySkuList?.setOnClickListener {
                val inflater = LayoutInflater.from(activity)
                val popupView = inflater.inflate(R.layout.drop_down_listview, null)
                val listView = popupView.findViewById<ListView>(R.id.listView)

                val popupWindow = PopupWindow(
                    popupView,
                    binding?.laySkuList!!.width,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )

                val adapter = SkuListAdapter(requireContext(), skuInfoList)
                listView.adapter = adapter

                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        binding?.txtDiscountedPrice!!.text =
                            skuInfoList[position].currencySymbol + "" + skuInfoList[position].resellPrice
                        val s2 =
                            skuInfoList[position].currencySymbol + "" + skuInfoList[position].markupPrice
                        binding?.originalPrice!!.text = s2
                        parts = skuInfoList[position].skuAttr?.split("#")
                        binding?.txtSkuName!!.text = parts?.last()
                        binding?.offTV!!.text =
                            skuInfoList[position].discountPercentage + "\noff"
                        popupWindow.dismiss()
                    }

                showPopupWindow(binding?.laySkuList!!, popupWindow)
            }

            binding?.storeTV!!.setOnClickListener {
                val inflater = LayoutInflater.from(activity)
                val popupView = inflater.inflate(R.layout.store_info_popup, null)

                val txtStoreName = popupView.findViewById<TextView>(R.id.txtStoreName)
                val txtStoreNumber = popupView.findViewById<TextView>(R.id.txtStoreNumber)
                val txtStoreLocation = popupView.findViewById<TextView>(R.id.txtStoreLocation)
                val txtItemAsDescribedRatings =
                    popupView.findViewById<TextView>(R.id.txtItemAsDescribedRatings)
                val txtCommunicationRatings =
                    popupView.findViewById<TextView>(R.id.txtCommunicationRatings)
                val txtDeliveryTimesRatings =
                    popupView.findViewById<TextView>(R.id.txtDeliveryTimesRatings)

                txtStoreName.text = productInfo.storeInfo?.storeName
                txtStoreNumber.text = productInfo.storeInfo?.storeId
                txtStoreLocation.text = productInfo.storeInfo?.storeCountryCode
                txtItemAsDescribedRatings.text = productInfo.storeInfo?.itemAsDescribedRating
                txtCommunicationRatings.text = productInfo.storeInfo?.communicationRating
                txtDeliveryTimesRatings.text = productInfo.storeInfo?.shippingSpeedRating

                val popupWindow = PopupWindow(
                    popupView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )

                showPopupWindow(binding?.storeTV!!, popupWindow)
            }

            binding?.imgShare?.setOnClickListener {
                val inflater = LayoutInflater.from(activity)
                val popupView = inflater.inflate(R.layout.share_product_popup, null)

                val imgShareFB = popupView.findViewById<AppCompatImageView>(R.id.imgShareFB)
                val imgSharePinterest =
                    popupView.findViewById<AppCompatImageView>(R.id.imgSharePinterest)
                val imgShareTwitter =
                    popupView.findViewById<AppCompatImageView>(R.id.imgShareTwitter)

                val popupWindow = PopupWindow(
                    popupView,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )

                imgShareFB.setOnClickListener {
                    popupWindow.dismiss()
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${product.title}\n${product.detailUrl}"
                            )
                            `package` = "com.facebook.katana"
                        }
                        context?.startActivity(shareIntent)
                    } catch (e: Exception) {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/sharer/sharer.php?u=${product.detailUrl}")
                        )
                        context?.startActivity(browserIntent)
                    }
                }

                imgSharePinterest.setOnClickListener {
                    popupWindow.dismiss()
                    try {
                        val pinterestIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${product.title}\n${product.detailUrl}"
                            )
                            putExtra(Intent.EXTRA_STREAM, Uri.parse(product.imageUrl))
                            `package` = "com.pinterest"
                        }
                        context?.startActivity(pinterestIntent)
                    } catch (e: Exception) {
                        val pinterestUrl = "https://www.pinterest.com/pin/create/button/" +
                                "?url=${Uri.encode(product.detailUrl)}" +
                                "&media=${Uri.encode(product.imageUrl)}" +
                                "&description=${Uri.encode(product.title)}"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pinterestUrl))
                        context?.startActivity(browserIntent)
                    }
                }

                imgShareTwitter.setOnClickListener {
                    popupWindow.dismiss()
                    try {
                        val tweetIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${product.title}\n${product.detailUrl}"
                            )
                            `package` = "com.twitter.android"
                        }
                        context?.startActivity(tweetIntent)
                    } catch (e: Exception) {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://twitter.com/intent/tweet?text=${
                                    Uri.encode(
                                        product.title
                                    )
                                }&url=${Uri.encode(product.detailUrl)}"
                            )
                        )
                        context?.startActivity(browserIntent)
                    }
                }
                showPopupWindow(binding?.imgShare!!, popupWindow)
            }

//            binding?.container?.setOnTouchListener { _, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_UP -> {
////                        showFullScreenImage()// Simulate click
//                        true
//                    }
//                    else -> false
//                }
//            }
        }

        marketPlacesViewModel.freightEstimateResponse.observe(viewLifecycleOwner) { data ->
            if (freightEstimateResponse.isEmpty()) {
                freightEstimateResponse = data
            }
            if (freightEstimateResponse.size > 0) {
                binding?.layFreightEstimate?.visibility = View.VISIBLE
                if (freightEstimateResponse[0].freeShipping.equals("true"))
                    binding?.tvShippingFees?.text = "Free Shipping"
                else binding?.tvShippingFees?.text =
                    "Shipping: " + freightEstimateResponse[0].shippingFeeFormat

                binding?.tvDeliveryDates?.text =
                    "Delivery: " + freightEstimateResponse[0].deliveryDateDesc
            } else {
                binding?.layFreightEstimate?.visibility = View.GONE
            }
//            hideProgressView()
        }

        binding?.showAllFreightEstimate?.setOnClickListener {
            val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.full_screen_freight_estimate_list, null)
            dialog.setContentView(view)
            val topMargin =
                requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
                    .toInt() // Top margin in pixels
            val bottomMargin =
                requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
                    .toInt() // Bottom margin in pixels

            val listFreightEstimate = view.findViewById<ListView>(R.id.listFreightEstimate)
            val imgClose = view.findViewById<ImageView>(R.id.imgClose)
            freightEstimateAdapter =
                FreightEstimateAdapter(requireActivity(), freightEstimateResponse, this)
            listFreightEstimate.adapter = freightEstimateAdapter

            imgClose.setOnClickListener {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }

            dialog.window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    requireActivity().resources.displayMetrics.heightPixels - (topMargin + bottomMargin)
                )
                setBackgroundDrawableResource(android.R.color.transparent)

                // Adjust the position of the dialog to respect the top margin
                attributes = attributes.apply {
                    gravity = Gravity.TOP
                    y = topMargin
                }
            }
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)

            dialog.show()
        }

        binding?.btnDecrease?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding?.tvQuantity?.text = quantity.toString()
            }
        }
        binding?.btnIncrease?.setOnClickListener {
            if (quantity < availableQuantity)
                quantity++
            binding?.tvQuantity?.text = quantity.toString()
        }

        binding?.addToCart?.setOnClickListener {
            context?.let {
                marketPlacesViewModel.addCartItem(
                    it,
                    userId,
                    productId1,
                    skuId,
                    quantity.toString()
                )
            }
        }

        binding?.imgCart?.setOnClickListener {
            val bundle = Bundle().apply {
//                putString("productId", productId)
//                putParcelable("selectedProduct", product)
//                putInt("itemPosition", position)
            }
            findNavController().navigate(
                destinationId = R.id.action_fragmentProduct_to_cartFragment,
                args = bundle
            )
        }

        binding?.buyNow?.setOnClickListener {
            val bundle = Bundle().apply {
//                putString("productId", productId)
//                putParcelable("selectedProduct", product)
//                putInt("itemPosition", position)
            }
            findNavController().navigate(
                destinationId = R.id.action_fragmentProduct_to_buyNowFragment,
                args = bundle
            )
        }
    }

    class ImageSliderAdapter1(
        private val photoList: ArrayList<Photo>
    ) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val context = container.context
            val frameLayout = FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val imageView = ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            // Load image using Glide or Picasso
            Glide.with(container.context)
                .load(photoList[position].url)
                .into(imageView)

            val textView = TextView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.START // Position the TextView to the top-right
                    topMargin = activity?.resources?.getDimension(com.intuit.sdp.R.dimen._25sdp)!!
                        .toInt() // Add some margin to the top
                }
                text = photoList[position].resellPrice // Set the price text
                setTextColor(Color.BLACK) // Set the text color
                setBackgroundResource(R.drawable.follow_left) // Optional: Set background color for better visibility
                setPadding(
                    activity?.resources?.getDimension(com.intuit.sdp.R.dimen._5sdp)
                    !!.toInt(), activity?.resources?.getDimension(com.intuit.sdp.R.dimen._5sdp)
                    !!.toInt(), activity?.resources?.getDimension(com.intuit.sdp.R.dimen._5sdp)
                    !!.toInt(), activity?.resources?.getDimension(com.intuit.sdp.R.dimen._5sdp)
                    !!.toInt()
                ) // Add padding inside the TextView
            }

            frameLayout.addView(imageView)
            if (photoList[position].resellPrice.isNotEmpty())
                frameLayout.addView(textView)

            container.addView(frameLayout)
            return frameLayout
        }

        override fun getCount(): Int = photoList.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    data class ColorInfo(
        val colorId: String,
        val colorImage: String,
        val resellPrice: String,
        val currencySymbol: String,
        val availableStock: String,
        val skuId: String,
        val items: MutableList<FilterSizeSkus> // Use the appropriate type for your list items
    )

    private fun showPopupWindow(view: View, popupWindow: PopupWindow) {
        popupWindow.showAsDropDown(
            view,
            0,
            0,
            Gravity.NO_GRAVITY
        )
    }

    @SuppressLint("SuspiciousIndentation")
    override fun setupTheme() {
        binding?.model = marketPlacesViewModel

        val productId = arguments?.getString("productId")
        product = arguments?.getParcelable<Products>("selectedProduct")!!
        if (productId1.isEmpty() || productId1 != productId)
            productId?.let { productDetails(it) }
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            getTypeActivity<MainActivity>()?.onBackPressed()
        }
    }

    private fun productDetails(productId: String) {
        showProgressView()
        context?.let {
            marketPlacesViewModel.productDetails(it, productId, "US")
        }
        return
    }

    class ImageSliderAdapter(
        val activity: Activity,
        private val photoList: ArrayList<Photo>
    ) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            // Load image using Glide or Picasso
            Glide.with(container.context)
                .load(photoList[position].url)
                .into(imageView)

            container.addView(imageView)

            imageView.setOnClickListener {
                showFullScreenImage(activity, position)
            }
            return imageView
        }

        private fun showFullScreenImage(activity: Activity, position: Int) {
            val dialog = Dialog(activity, R.style.TransparentDialog)

            // Inflate the full-screen dialog layout
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.full_screen_image_list, null)

            // Set the PhotoView
            val container = view.findViewById<ViewPager>(R.id.container)
            val imgLeft = view.findViewById<AppCompatImageView>(R.id.imgLeft)
            val imgRight = view.findViewById<AppCompatImageView>(R.id.imgRight)
            val imgClose = view.findViewById<ImageView>(R.id.imgClose)

            imgLeft.setOnClickListener {
                if (container.currentItem > 0)
                    container.setCurrentItem(container.currentItem - 1)
            }

            imgRight.setOnClickListener {
                if (container.currentItem < photoList.size - 1)
                    container.setCurrentItem(container.currentItem + 1)
            }

            val adapter = activity.fragmentManager?.let {
                ImageSliderAdapter1(
                    photoList
                )
            }!!
            container?.adapter = adapter
            container?.setCurrentItem(position)

            // Create and show the dialog
            dialog.setContentView(view)

            // Define top and bottom margins
            val topMargin =
                activity.resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
                    .toInt() // Top margin in pixels
            val bottomMargin = activity.resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
                .toInt() // Bottom margin in pixels

            dialog.window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    activity.resources.displayMetrics.heightPixels - (topMargin + bottomMargin)
                )
                setBackgroundDrawableResource(android.R.color.transparent)

                // Adjust the position of the dialog to respect the top margin
                attributes = attributes.apply {
                    gravity = android.view.Gravity.TOP
                    y = topMargin
                }
            }
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)

            dialog.show()

            imgClose.setOnClickListener {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }

        override fun getCount(): Int {
            return photoList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    override fun onClickColorItemListener(filterColorSkus: FilterColorSkus, position: Int) {
        binding?.txtSelectedColorName?.text = "Color: " + filterColorSkus.color
        val floatNumber = filterColorSkus.availableStock.toFloatOrNull()
        availableQuantity = floatNumber!!.toInt()
        if (availableQuantity > 0)
            quantity = 1 else quantity = 0
        binding?.tvQuantity?.text = quantity.toString()
        binding?.txtDiscountedPrice!!.text =
            filterColorSkus.currencySymbol + "" + filterColorSkus.resellPrice
        if (skuId != filterColorSkus.skuId) {
            skuId = filterColorSkus.skuId
            freightEstimateResponse.clear()
            context?.let {
                marketPlacesViewModel.freightEstimate(
                    it,
                    productId1,
                    quantity.toString(),
                    filterColorSkus.skuId
                )
            }
        }
        binding?.tvQuantityAvailable?.text = "$availableQuantity available"
        if (filterColorSkus.filterSizeSkus.isNotEmpty()) {
            binding?.recyclerSizes?.visibility = View.VISIBLE
            binding?.txtSelectedSizeName?.visibility = View.VISIBLE
            skuSizeAdapter =
                SkuSizeAdapter(
                    activity,
                    filterColorSkus.filterSizeSkus,
                    this
                )
            binding?.recyclerSizes?.adapter = skuSizeAdapter
        } else {
            binding?.recyclerSizes?.visibility = View.GONE
            binding?.txtSelectedSizeName?.visibility = View.GONE
        }
    }

    override fun onClickSizeItemListener(filterSizeSkus: FilterSizeSkus) {
        binding?.txtSelectedSizeName?.text =
            filterSizeSkus.skuPropertyName + ": " + filterSizeSkus.size
    }

    override fun onShowColorItemListener(filterColorSkus: FilterColorSkus, position: Int) {
        for (i in photoList.indices) {
            if (photoList[i].url == filterColorSkus.skuImage && photoList[i].resellPrice == filterColorSkus.currencySymbol + "" + filterColorSkus.resellPrice) {
                binding?.container?.setCurrentItem(i)
                showFullScreenImage(i, position)
            }
        }
    }

    private fun showFullScreenImage(position1: Int, position2: Int) {
        val dialog = Dialog(requireActivity(), R.style.TransparentDialog)

        // Inflate the full-screen dialog layout
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.full_screen_image_list_1, null)

        // Set the PhotoView
        val container1 = view.findViewById<ViewPager>(R.id.container)
        val imgLeft = view.findViewById<AppCompatImageView>(R.id.imgLeft)
        val imgRight = view.findViewById<AppCompatImageView>(R.id.imgRight)
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        val txtSelectedColorName1 = view.findViewById<TextView>(R.id.txtSelectedColorName1)
        val txtSelectedSizeName1 = view.findViewById<TextView>(R.id.txtSelectedSizeName1)
        val recyclerColor1 = view.findViewById<RecyclerView>(R.id.recyclerColor1)
        val recyclerSizes1 = view.findViewById<RecyclerView>(R.id.recyclerSizes1)
        val btnDecrease1 = view.findViewById<ImageView>(R.id.btnDecrease1)
        val btnIncrease1 = view.findViewById<ImageView>(R.id.btnIncrease1)
        val tvQuantity1 = view.findViewById<TextView>(R.id.tvQuantity1)

        btnDecrease1.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding?.tvQuantity?.text = quantity.toString()
                tvQuantity1?.text = quantity.toString()
            }
        }

        btnIncrease1.setOnClickListener {
            if (quantity < availableQuantity)
                quantity++
            binding?.tvQuantity?.text = quantity.toString()
            tvQuantity1?.text = quantity.toString()
        }

        imgLeft.setOnClickListener {
            if (container1.currentItem > 0) {
                val c1 = container1.currentItem - 1
                container1.setCurrentItem(c1)
            }
        }

        imgRight.setOnClickListener {
            if (container1.currentItem < photoList.size - 1) {
                val c1 = container1.currentItem + 1
                container1.setCurrentItem(c1)
            }
        }

        val adapter = requireActivity().fragmentManager?.let {
            ImageSliderAdapter1(
                photoList
            )
        }!!
        container1?.adapter = adapter
        container1?.setCurrentItem(position1)
//        txtSelectedColorName1.setText(photoList[position1].c)
        recyclerColor1?.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerSizes1?.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        val skuColorAdapter1 = SkuColorAdapter(
            activity,
            filterColorSkus,
            object : SkuColorAdapter.ClickColorItemListener {
                override fun onClickColorItemListener(
                    filterColorSkus: FilterColorSkus,
                    position: Int
                ) {
                    txtSelectedColorName1?.text = "Color: " + filterColorSkus.color
                    val floatNumber = filterColorSkus.availableStock.toFloatOrNull()
                    availableQuantity = floatNumber!!.toInt()
                    if (availableQuantity > 0)
                        quantity = 1 else quantity = 0
                    binding?.tvQuantity?.text = quantity.toString()
                    tvQuantity1.text = quantity.toString()
                    binding?.txtDiscountedPrice!!.text =
                        filterColorSkus.currencySymbol + "" + filterColorSkus.resellPrice
                    if (filterColorSkus.filterSizeSkus.isNotEmpty()) {
                        recyclerSizes1?.visibility = View.VISIBLE
                        txtSelectedSizeName1?.visibility = View.VISIBLE
                        val skuSizeAdapter1 = SkuSizeAdapter(
                            activity,
                            filterColorSkus.filterSizeSkus,
                            object : SkuSizeAdapter.ClickSizeItemListener {
                                override fun onClickSizeItemListener(filterSizeSkus: FilterSizeSkus) {
                                    txtSelectedSizeName1.text =
                                        filterSizeSkus.skuPropertyName + ": " + filterSizeSkus.size
                                }
                            })
                        recyclerSizes1.adapter = skuSizeAdapter1
                    } else {
                        recyclerSizes1?.visibility = View.GONE
                        txtSelectedSizeName1?.visibility = View.GONE
                    }
                }
            },
            object : SkuColorAdapter.ShowImageOnViewPager {
                override fun onShowColorItemListener(
                    filterColorSkus: FilterColorSkus,
                    position: Int
                ) {
                    skuColorAdapter.setSelectedItem(position)
                    for (i in photoList.indices) {
                        if (photoList[i].url == filterColorSkus.skuImage && photoList[i].resellPrice == filterColorSkus.currencySymbol + "" + filterColorSkus.resellPrice) {
                            container1?.setCurrentItem(i)
                        }
                    }
                }
            }
        )
        recyclerColor1.adapter = skuColorAdapter1
        skuColorAdapter1.setSelectedItem(position2)

        container1?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding?.container?.setCurrentItem(position)
                filterColorSkus.forEachIndexed { index, value ->
                    if (photoList[position].skuPropertyId == value.skuPropertyId) {
                        skuColorAdapter.setSelectedItem(index)
                        skuColorAdapter1.setSelectedItem(index)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        // Create and show the dialog
        dialog.setContentView(view)

        // Define top and bottom margins
        val topMargin =
            requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._100sdp)
                .toInt() // Top margin in pixels
        val bottomMargin = requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
            .toInt() // Bottom margin in pixels

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                requireActivity().resources.displayMetrics.heightPixels - (topMargin + bottomMargin)
            )
            setBackgroundDrawableResource(android.R.color.transparent)

            // Adjust the position of the dialog to respect the top margin
            attributes = attributes.apply {
                gravity = Gravity.TOP
                y = topMargin
            }
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.show()

        imgClose.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    private fun setHtmlWithImages(textView: TextView, htmlText: String) {
        textView.post {
            val imageGetter = Html.ImageGetter { source ->
                val placeholder = textView.context.getDrawable(android.R.drawable.ic_menu_gallery)
                val desiredWidth = textView.width // Or a fixed width in pixels
                val desiredHeight = (desiredWidth * 0.75).toInt()
                placeholder?.setBounds(0, 0, desiredWidth, desiredHeight)

                val drawableWrapper = LevelListDrawable().apply {
                    addLevel(0, 0, placeholder)
                    setBounds(0, 0, desiredWidth, desiredHeight)
                }

                // Load the image using Glide
                Glide.with(textView.context)
                    .load(source)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            resource.setBounds(
                                0,
                                0,
                                resource.intrinsicWidth,
                                resource.intrinsicHeight
                            )
                            drawableWrapper.addLevel(1, 1, resource)
                            drawableWrapper.setLevel(1)

                            // Refresh TextView
                            textView.text = textView.text
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })

                drawableWrapper
            }

            val spannedText: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
            } else {
                Html.fromHtml(htmlText, imageGetter, null)
            }

            textView.post {
                textView.text = spannedText
            }
            textView.movementMethod = LinkMovementMethod.getInstance()

            // Set a custom movement method to handle link clicks
            textView.movementMethod = object : LinkMovementMethod() {
                private var downX = 0f
                private var downY = 0f
                private val touchSlop = ViewConfiguration.get(textView.context).scaledTouchSlop

                override fun onTouchEvent(
                    widget: TextView,
                    buffer: Spannable,
                    event: MotionEvent
                ): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            downX = event.x
                            downY = event.y
                        }

                        MotionEvent.ACTION_UP -> {
                            val deltaX = event.x - downX
                            val deltaY = event.y - downY

                            if (Math.abs(deltaX) < touchSlop && Math.abs(deltaY) < touchSlop) {
                                val imageSpan = getImageSpanHit(widget, buffer, event)
                                if (imageSpan != null) {
                                    showImageDialog(imageSpan.source)
                                    return true
                                }
                            }
                        }
                    }
                    return super.onTouchEvent(widget, buffer, event)
                }
            }
        }
    }

    private fun showImageDialog(imageUrl: String?) {
        imageUrl?.let {
            val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.full_screen_image, null)
            dialog.setContentView(view)

            val photoView = view.findViewById<PhotoView>(R.id.photoView)
            val imgClose = view.findViewById<ImageView>(R.id.imgClose)

            photoView.loadImage(it) // Load image into PhotoView

            val topMargin =
                requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
            val bottomMargin =
                requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()

            dialog.window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    requireActivity().resources.displayMetrics.heightPixels - (topMargin + bottomMargin)
                )
                setBackgroundDrawableResource(android.R.color.transparent)
                attributes = attributes.apply {
                    gravity = Gravity.TOP
                    y = topMargin
                }
            }

            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.show()

            imgClose.setOnClickListener {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun getImageSpanHit(
        widget: TextView,
        buffer: Spannable,
        event: MotionEvent
    ): ImageSpan? {
        val x = (event.x - widget.totalPaddingLeft + widget.scrollX).toInt()
        val y = (event.y - widget.totalPaddingTop + widget.scrollY).toInt()

        val layout = widget.layout ?: return null
        val imageSpans = buffer.getSpans(0, buffer.length, ImageSpan::class.java)

        val margin = 10f // Consider increasing the margin if spans are too close
        val matchingSpans = imageSpans.filter { span ->
            val start = buffer.getSpanStart(span)
            val end = buffer.getSpanEnd(span)
            val spanBounds = calculateSpanBounds(widget, layout, start, end)

            // Check if the touch is within bounds, adjusted by margin
            x.toFloat() > spanBounds.left + margin &&
                    x.toFloat() < spanBounds.right - margin &&
                    y.toFloat() > spanBounds.top + margin &&
                    y.toFloat() < spanBounds.bottom - margin
        }

        // Find the closest span based on touch distance
        return matchingSpans.minByOrNull { span ->
            val start = buffer.getSpanStart(span)
            val end = buffer.getSpanEnd(span)
            val spanBounds = calculateSpanBounds(widget, layout, start, end)

            val centerX = (spanBounds.left + spanBounds.right) / 2
            val centerY = (spanBounds.top + spanBounds.bottom) / 2
            val dx = x - centerX
            val dy = y - centerY

            dx * dx + dy * dy
        }
    }

    private fun calculateSpanBounds(
        widget: TextView,
        layout: Layout,
        start: Int,
        end: Int
    ): RectF {
        val path = Path()
        layout.getSelectionPath(start, end, path)

        val bounds = RectF()
        path.computeBounds(bounds, true)

        // Adjust bounds for padding and scroll
        bounds.offset(
            widget.totalPaddingLeft.toFloat() - widget.scrollX,
            widget.totalPaddingTop.toFloat() - widget.scrollY
        )
        return bounds
    }

    override fun onSelectSingleFreightEstimate(item: FreightEstimateResponse) {

    }
}