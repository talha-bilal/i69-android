package com.i69.ui.screens.main.market.store

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.data.enums.HttpStatusCode
import com.i69.data.models.market.Category
import com.i69.data.models.market.FreightEstimateResponse
import com.i69.data.models.market.GetAllCategoriesResponse
import com.i69.data.models.market.GetChildrenCategoriesResponse
import com.i69.data.models.market.Products
import com.i69.data.models.market.SearchProducts
import com.i69.data.preferences.UserPreferences
import com.i69.data.remote.api.MarketGraphqlApi
import com.i69.data.remote.responses.ResponseBody
import com.i69.databinding.DialogImageOptionBinding
import com.i69.databinding.DialogImageSearchMarketBinding
import com.i69.databinding.FragmentStoreBinding
import com.i69.di.modules.AppModule
import com.i69.singleton.App
import com.i69.ui.adapters.CategoryAdapter
import com.i69.ui.adapters.PhotosData
import com.i69.ui.adapters.ProductsAdapter
import com.i69.ui.screens.ImagePickerActivity
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.camera.CameraActivity
import com.i69.utils.AnimationTypes
import com.i69.utils.PaginationScrollListener
import com.i69.utils.Resource
import com.i69.utils.Utils
import com.i69.utils.convertURITOBitmapNSaveImage
import com.i69.utils.copyToClipboard
import com.i69.utils.createLoadingDialog
import com.i69.utils.getGraphqlApiBody
import com.i69.utils.navigate
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

@AndroidEntryPoint
class StoreFragment : Fragment(), ProductsAdapter.SharedProductListener,
    CategoryAdapter.ClickExpandCategoryListener, CategoryAdapter.ChildCategoryClickListener,
    CategoryAdapter.ClickCategoryListener {

    //    private val marketPlacesViewModel: MarketPlacesViewModel by activityViewModels()
    private var binding: FragmentStoreBinding? = null
    private var userToken: String? = null
    private var searchText: String? = ""
    private var searchCategoryId: String? = ""
    private lateinit var productAdapter: ProductsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList = mutableListOf<Category>()
    private val productList = mutableListOf<Products>()
    private var imageType = ""
    private var minPrice = ""
    private var maxPrice = ""
    private var sortPrice = ""
    var isLoading1: Boolean = false
    private val pageSize: Int = 25
    var pageIndex: Int = 1
    var isNextPageCall: Boolean = false
    var isNextPage: Boolean = true
    var TAG: String = StoreFragment::class.java.simpleName
    private lateinit var popupWindow: PopupWindow
    private var mFilePath: String = ""
    private lateinit var contentUri: Uri
    lateinit var file: File
    lateinit var fileType: String
    private var currentApiCall = ""
    private var imageString = ""
    private var isDataLoaded = false
    private var userPreferences1: UserPreferences? = null
    suspend fun getCurrentUserToken() = userPreferences1?.userToken?.first()
    var loadingDialog: Dialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userPreferences1 = App.userPreferences
    }

    private val photosLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                mFilePath = data?.getStringExtra("result").toString()
                contentUri =
                    Uri.fromFile(File(mFilePath)) ?: throw IllegalArgumentException("No data URI")
                file = File(mFilePath)
                if (mFilePath.contains(".")) {
                    val regex = "\\.".toRegex()
                    val type: String =
                        mFilePath.reversed().split(regex).get(0).reversed().toString()
                    fileType = ".$type"
                }
                if (Utils.isImageFile(
                        contentUri,
                        requireContext()
                    ) || Utils.isImageFile(contentUri)
                ) {
                    val imageUri = Uri.fromFile(mFilePath.let { File(it) })
                    CropImage.activity(imageUri)
                        .start(requireContext(), this)
                }
            }
        }

    private val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {

                val pathOfFile = convertURITOBitmapNSaveImage(
                    requireContext(),
                    data?.data!!
                )
                val photoData = PhotosData(pathOfFile!!, imageType)

                mFilePath = data.data.toString()
                contentUri = data.data ?: throw IllegalArgumentException("No data URI")
                val openInputStream =
                    requireActivity().contentResolver?.openInputStream(contentUri)
                        ?: throw IllegalStateException("Unable to open input stream")
                var type = ""
                if (mFilePath.contains(".")) {
                    val regex = "\\.".toRegex()
                    type = mFilePath.reversed().split(regex)?.get(0)?.reversed().toString()
                    fileType = ".$type"
                }
                fileType = if (Utils.isImageFile(contentUri, requireContext()) || Utils.isImageFile(
                        contentUri
                    )
                ) ".jpg" else ".mp4"

                val fileName = "${System.currentTimeMillis()}$fileType"
                val outputFile = File(requireContext().filesDir, fileName)
                openInputStream.use { inputStream ->
                    outputFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                file = outputFile
                if (Utils.isImageFile(
                        contentUri,
                        requireContext()
                    ) || Utils.isImageFile(contentUri)
                ) {
                    val compressedPath = convertURITOBitmapNSaveImage(
                        requireContext(),
                        data?.data!!,
                        outputFile,
                    )
                    compressedPath?.let {
                        file = File(it)
                    }

                    val imageUri = data.data
                    CropImage.activity(imageUri)
                        .start(requireContext(), this)
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val pathOfFile = result.uri.path
                file = File(pathOfFile)
                if (pathOfFile != null) {
                    mFilePath = pathOfFile
                }
                contentUri = Uri.fromFile(file)

                val inputStream: InputStream? =
                    context?.contentResolver?.openInputStream(contentUri)
                if (inputStream != null) {
                    // Decode InputStream to Bitmap
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    // Convert Bitmap to Base64
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    // Compress the Bitmap to JPEG format and write it to the output stream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    // Encode the byte array to Base64 string
                    imageString = Base64.encodeToString(byteArray, Base64.NO_WRAP)

                    currentApiCall = "searchImage"
                    showProgressView()
                    pageIndex = 1
                    productList.clear()
                    productAdapter = ProductsAdapter(activity, this)
                    binding?.recyclerViewMarket?.adapter = productAdapter

                    searchProducts(
                        searchText!!,
                        searchCategoryId,
                        minPrice,
                        maxPrice,
                        "clear",
                        sortPrice
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun showProgressView() {
        if (loadingDialog != null && loadingDialog?.isShowing == false) {
            loadingDialog?.show()
        }
    }

    private fun hideProgressView() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
        }
    }

    protected fun <T : Activity> getTypeActivity(): T? {
        return if (activity != null) activity as T else null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTypeActivity<MainActivity>()?.reloadNavigationMarketMenu()
        loadingDialog = requireActivity().createLoadingDialog()

        binding?.recyclerViewMarket?.setHasFixedSize(true)
        val layoutManager1 = GridLayoutManager(context, 2)
        binding?.recyclerViewMarket?.apply {
            layoutManager = layoutManager1
        }
        productAdapter = ProductsAdapter(activity, this)
        binding?.recyclerViewMarket?.adapter = productAdapter

        if (!isDataLoaded) {
            showProgressView()
            pageIndex = 1
            productList.clear()
            productAdapter.clearAdapter()
//        searchText = "CLOTHES for adults"
            currentApiCall = "feedItems"
            searchText = ""
            searchProducts(searchText!!, searchCategoryId, minPrice, maxPrice, "clear", sortPrice)
//        getFeedItems(searchText!!, searchCategoryId, minPrice, maxPrice)
        } else {
            productAdapter.addProducts(productList)
            isDataLoaded = false
        }

        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            if (categoryList.size <= 1) {
                categoryList.clear()
                categoryList.add(Category("", "Select Category", ""))
                context?.let { categoryAPI() }
            }
        }

        binding?.recyclerViewMarket?.addOnScrollListener(object :
            PaginationScrollListener(layoutManager1) {
            override fun loadMoreItems() {
                if (isNextPage) {
                    isLoading1 = true
                    pageIndex++
                    isNextPageCall = true
                    searchProducts(
                        searchText!!,
                        searchCategoryId,
                        minPrice,
                        maxPrice,
                        "",
                        sortPrice
                    )
                }
            }

            override fun isLastPage(): Boolean {
                return false
            }

            override fun isLoading(): Boolean {
                return isLoading1
            }
        })

        binding?.categoryLL?.setOnClickListener {
            if (categoryList.size > 1) {
                val inflater = LayoutInflater.from(activity)
                val popupView = inflater.inflate(R.layout.drop_down_listview, null)
                val listView = popupView.findViewById<ListView>(R.id.listView)

                popupWindow = PopupWindow(
                    popupView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )

                listView.adapter = categoryAdapter

                popupWindow.showAsDropDown(
                    binding?.categoryLL!!,
                    0,
                    0,
                    Gravity.NO_GRAVITY
                )
            }
        }

        binding?.layPriceRange?.setOnClickListener {
            val inflater = LayoutInflater.from(activity)
            val popupView = inflater.inflate(R.layout.price_range_popup, null)

            popupWindow = PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            )

            val layPriceRange = popupView.findViewById<LinearLayout>(R.id.layPriceRange)
            val radioGroupSort = popupView.findViewById<RadioGroup>(R.id.radioGroupSort)
            val edtPriceFrom = popupView.findViewById<EditText>(R.id.edtPriceFrom)
            val edtPriceTo = popupView.findViewById<EditText>(R.id.edtPriceTo)
            val txtApplyPriceRange = popupView.findViewById<TextView>(R.id.txtApplyPriceRange)

            edtPriceFrom.setText(minPrice)
            edtPriceTo.setText(maxPrice)

            if (currentApiCall == "searchProducts" || currentApiCall == "searchImage") {
                radioGroupSort.visibility = View.GONE
                layPriceRange.visibility = View.VISIBLE
            } else {
                radioGroupSort.visibility = View.VISIBLE
                layPriceRange.visibility = View.GONE
            }

            txtApplyPriceRange.setOnClickListener {
                if (searchText!!.trim().isNotEmpty()) {
                    minPrice = edtPriceFrom.text.toString()
                    maxPrice = edtPriceTo.text.toString()

                    val minValue = minPrice.toDoubleOrNull()
                    val maxValue = maxPrice.toDoubleOrNull()

                    if (edtPriceFrom.text.toString().isNotEmpty() && edtPriceTo.text.toString()
                            .isNotEmpty() && (minValue!! > maxValue!!)
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Maximum amount should greater than minimum amount",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (edtPriceFrom.text.toString().trim().isEmpty())
                            minPrice = ""
                        if (edtPriceTo.text.toString().trim().isEmpty())
                            maxPrice = ""
                        sortPrice = ""
                        searchProducts(
                            searchText!!,
                            searchCategoryId,
                            minPrice,
                            maxPrice,
                            "clear",
                            sortPrice
                        )
                    }
                    popupWindow.dismiss()
                } else {
                    val selectedId = radioGroupSort.checkedRadioButtonId
                    minPrice = ""
                    maxPrice = ""
                    if (selectedId != -1) {
                        if (selectedId == R.id.radioAscending) {
                            sortPrice = "min_price_asc"
                            searchProducts(
                                searchText!!,
                                searchCategoryId,
                                minPrice,
                                maxPrice,
                                "clear",
                                sortPrice
                            )
                        } else if (selectedId == R.id.radioDescending) {
                            sortPrice = "min_price_desc"
                            searchProducts(
                                searchText!!,
                                searchCategoryId,
                                minPrice,
                                maxPrice,
                                "clear",
                                sortPrice
                            )
                        }
                        popupWindow.dismiss()
                    }
                }
            }

            popupWindow.showAsDropDown(
                binding?.layPriceRange!!,
                0,
                0,
                Gravity.END
            )
        }

        initSearch()

        binding?.toolbarHamburger?.setOnClickListener { (activity as MainActivity).drawerSwitchState() }

        binding?.imageSearchIV?.setOnClickListener { showImageSearchDialog() }

        binding?.searchtIV?.setOnClickListener {
            binding?.SearchLL?.visibility = View.VISIBLE
            binding?.actionIconLL?.visibility = View.GONE
            binding?.title?.visibility = View.GONE
        }
        binding?.cross?.setOnClickListener {
            binding?.SearchLL?.visibility = View.GONE
            binding?.actionIconLL?.visibility = View.VISIBLE
            binding?.title?.visibility = View.VISIBLE
        }
    }


    private fun initSearch() {
        binding?.keyInput?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchText = binding?.keyInput?.text.toString()
                if (searchText!!.trim().isNotEmpty()) {
                    currentApiCall = "searchProducts"
                } else currentApiCall = "feedItems"
                searchCategoryId = ""
                binding?.txtCategoryName?.text = categoryList[0].categoryName
                searchProducts(
                    searchText!!,
                    searchCategoryId,
                    minPrice,
                    maxPrice,
                    "clear",
                    sortPrice
                )
                return@OnEditorActionListener true
            }
            false
        })

        binding?.keyInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding?.keyInput?.text?.length != 0) {

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onProductClick(position: Int, productId: String, product: Products) {
        isDataLoaded = true
        val bundle = Bundle().apply {
            putString("productId", productId)
            putParcelable("selectedProduct", product)
            putInt("itemPosition", position)
        }
        findNavController().navigate(
            destinationId = R.id.action_StoreFragment_to_fragmentProduct,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }

    private fun showImageSearchDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogImageSearchMarketBinding.inflate(layoutInflater)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogBinding.ivCross.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.SearchCL.setOnClickListener {
            dialog.dismiss()
            showChooseImageDialog()
        }

        dialogBinding.selctImage.setOnClickListener {
            dialog.dismiss()
            showChooseImageDialog()
        }

        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun showChooseImageDialog() {
        val dialog = Dialog(requireContext())
        val view = DialogImageOptionBinding.inflate(layoutInflater)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        view.headerTitle.text = "Select Image"

        val llCamera = view.llCamera
        val llGalerry = view.llGallery

        llCamera.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent(requireActivity(), CameraActivity::class.java)
            } else {
                Intent(requireActivity(), ImagePickerActivity::class.java)
            }
            intent.putExtra("video_duration_limit", 60)
            intent.putExtra("withCrop", false)
            photosLauncher.launch(intent)
            dialog.dismiss()
        }

        llGalerry.setOnClickListener {
            galleryImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
            )
            dialog.dismiss()
        }
        dialog.setContentView(view.root)
        dialog.show()
        dialog.window?.attributes = lp
    }

//    private fun getFeedItems(
//        search: String, categoryId: String?, minPrice: String,
//        sortPrice: String
//    ) {
//        showProgressView()
//        context?.let {
//            marketPlacesViewModel.feedItems(
//                it,
//                search,
//                categoryId,
//                pageSize,
//                pageIndex, sortPrice
//            )
//        }
//    }

    private fun searchProducts(
        search: String, categoryId: String?, minPrice: String,
        maxPrice: String, clear: String, sortPrice: String
    ) {
        showProgressView()

        if (clear == "clear") {
            pageIndex = 1
            productList.clear()
            productAdapter = ProductsAdapter(activity, this)
            binding?.recyclerViewMarket?.adapter = productAdapter
        }
        if (currentApiCall == "searchProducts") context?.let {
            searchProductsAPI(
                search,
                categoryId,
                pageSize,
                pageIndex, minPrice, maxPrice
            )
        }
        else if (currentApiCall == "feedItems") context?.let {
            getFeedItemsApi(
                categoryId,
                pageSize,
                pageIndex, sortPrice
            )
        }
        else if (currentApiCall == "searchImage") context?.let {
            searchImageApi(imageString)
        }
        return
    }

    override fun onCategoryExpandClick(position: Int, categoryId: String, category: Category) {
        showProgressView()
        context?.let { getChildrenCategoriesApi(categoryId, position) }
    }

    fun onMenuCategoryItemClicked(category: Category) {
        searchCategoryId = category.categoryId
        currentApiCall = "searchProducts"
        searchText = ""
        imageString = ""
        binding?.keyInput?.setText(searchText)
        searchProducts(searchText!!, searchCategoryId, minPrice, maxPrice, "clear", sortPrice)
    }

    override fun onChildCategoryClick(childCategory: Category) {
        searchCategoryId = childCategory.categoryId
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
        currentApiCall = "searchProducts"
        searchText = ""
        imageString = ""
        binding?.keyInput?.setText(searchText)
        searchProducts(searchText!!, searchCategoryId, minPrice, maxPrice, "clear", sortPrice)
    }

    override fun onCategoryClick(category: Category) {
        binding?.txtCategoryName?.text = category.categoryName
        searchCategoryId = category.categoryId
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
        currentApiCall = "searchProducts"
        searchText = ""
        imageString = ""
        binding?.keyInput?.setText(searchText)
        searchProducts(searchText!!, searchCategoryId, minPrice, maxPrice, "clear", sortPrice)
    }

    private fun searchProductsAPI(
        searchText: String,
        categoryId: String?,
        pageSize: Int,
        pageIndex: Int,
        minPrice: String,
        maxPrice: String
    ) {

//        val queryName = "searchProducts"
        val queryName = "searchProductsStable"
        var queryNew = ""

        queryNew = buildString {
            append("mutation {")
            append("  searchProductsStable(searchText:\"$searchText\"")

            if (categoryId!!.isNotEmpty()) {
                append(", categoryId:\"$categoryId\"")
            }
            if (minPrice.isNotEmpty()) {
                append(", minPrice:${minPrice.toFloat()}")
            }
            if (maxPrice.isNotEmpty()) {
                append(", maxPrice:${maxPrice.toFloat()}")
            }

            append(", pageSize:$pageSize, pageIndex:$pageIndex) {")
            append("    products {")
            append("      productId")
            append("      title")
            append("      currency")
            append("      currencySymbol")
            append("      price")
            append("      markupPrice")
            append("      discountedPrice")
            append("      discount")
            append("      imageUrl")
            append("      detailUrl")
            append("      shopUrl")
            append("      firstLevelCategoryName")
            append("      score")
            append("      resellPrice")
            append("    }")
            append("  }")
            append("}")
        }

        lifecycleScope.launch {
            val result = AppModule.provideGraphqlApiMarket().getResponses<SearchProducts>(
                queryNew,
                queryName, "userToken"
            ).data?.data

            if (currentApiCall == "searchProducts") {
                productList.addAll(result!!.products)
                productAdapter.addProducts(result.products)
                isLoading1 = false
                if (result.products.size >= pageSize) {
                    isNextPage = true
                } else isNextPage = false
                hideProgressView()
            }
        }
    }

    private fun searchImageApi(imageString: String) {
        val queryName = "imageSearch"
        var queryNew = ""

        queryNew = buildString {
            append("query {")
            append("  $queryName(")

            append("shptTo:\"US\",")
            append("sort:\"RELEVANCE_ASC\",")
            append("imageFileBytes:\"$imageString\"")

            append(") {")
            append("    productId")
            append("    title")
            append("    currency")
            append("    currencySymbol")
            append("    price")
            append("    markupPrice")
            append("    discountedPrice")
            append("    resellPrice")
            append("    imageUrl")
            append("    detailUrl")
            append("    shopUrl")
            append("    firstLevelCategoryName")
            append("    secondLevelCategoryName")
            append("    score")
            append("    discount")
            append("  }")
            append("}")
        }

        lifecycleScope.launch {
            val result = AppModule.provideGraphqlApiMarket().getResponses<ArrayList<Products>>(
                queryNew, queryName, "userToken"
            ).data?.data

            if (currentApiCall == "searchImage") {
                productList.addAll(result!!)
                productAdapter.addProducts(result)
                isLoading1 = false
                if (result.size >= pageSize) {
                    isNextPage = true
                } else isNextPage = false
                hideProgressView()
            }
        }
    }

    private fun getFeedItemsApi(
        categoryId: String?,
        pageSize: Int,
        pageIndex: Int,
        sortPrice: String
    ) {

        val queryName = "feedItems"
        var queryNew = ""

        queryNew = buildString {
            append("query {")
            append("  $queryName(feedName: \"3SproductsUS\"")

            if (categoryId!!.isNotEmpty()) {
                append(", categoryId:\"$categoryId\"")
            }
            if (sortPrice.isNotEmpty()) {
//                append(", minPrice:${minPrice.toFloat()}")
                append(", sort:\"${sortPrice}\"")
            }

            append(", pageSize:$pageSize, pageNo:$pageIndex) {")
            append("      productId")
            append("      title")
            append("      currency")
            append("      currencySymbol")
            append("      price")
            append("      markupPrice")
            append("      discountedPrice")
            append("      discount")
            append("      imageUrl")
            append("      detailUrl")
            append("      shopUrl")
            append("      firstLevelCategoryName")
            append("      score")
            append("      resellPrice")
            append("    }")
            append("}")
        }

        lifecycleScope.launch {
            val result = AppModule.provideGraphqlApiMarket().getResponses<ArrayList<Products>>(
                queryNew,
                queryName, "userToken"
            ).data?.data
            if (result != null) {
                if (currentApiCall == "feedItems") {
                    productList.addAll(result)
                    productAdapter.addProducts(result)
                    isLoading1 = false
                    if (result.size >= pageSize) {
                        isNextPage = true
                    } else isNextPage = false
                    hideProgressView()
                }
            } else hideProgressView()
        }
    }

    private fun categoryAPI() {

        val queryName = "getAllCategories"

//        val query = "query {" +
//                "  getAllCategories(categoryId:\"201379402\") {" +
//                "    categoryId" +
//                "    categoryName" +
//                "    parentCategoryId" +
//                "  } " +
//                "}"

        val queryNew = "query{" +
                "  getAllCategories{" +
                "    categoryId" +
                "    categoryName" +
                "    parentCategoryId" +
                "  } " +
                "}"

        lifecycleScope.launch {
            val result =
                AppModule.provideGraphqlApiMarket()
                    .getResponses<ArrayList<GetAllCategoriesResponse>>(
                        queryNew,
                        queryName, "userToken"
                    ).data?.data

            if (categoryList.size <= 1) {
                result?.forEach {
                    categoryList.add(Category(it.categoryId, it.categoryName, it.parentCategoryId))
                }
                binding?.txtCategoryName?.text = categoryList[0].categoryName
                categoryAdapter = CategoryAdapter(
                    requireContext(),
                    categoryList,
                    this@StoreFragment,
                    this@StoreFragment,
                    this@StoreFragment
                )
            } else {
                binding?.txtCategoryName?.text = categoryList[0].categoryName
                categoryAdapter = CategoryAdapter(
                    requireContext(),
                    categoryList,
                    this@StoreFragment,
                    this@StoreFragment,
                    this@StoreFragment
                )
            }
        }
    }

    private fun getChildrenCategoriesApi(categoryId: String, position: Int) {
        val queryName = "getChildrenCategories"

        val query = """
    query getChildrenCategories {
        getChildrenCategories(categoryId: "$categoryId") {
            categoryId
            categoryName
            parentCategoryId
            level
            isLeaf
        }
    }
""".trimIndent()

        lifecycleScope.launch {
            val result = AppModule.provideGraphqlApiMarket()
                .getResponses<ArrayList<GetChildrenCategoriesResponse>>(
                    query, queryName, "userToken"
                ).data?.data

            val childCategoryList = mutableListOf<Category>()
            result!!.forEach {
                childCategoryList.add(Category(it.categoryId, it.categoryName, it.parentCategoryId))
            }
            hideProgressView()
            categoryAdapter.addExpandChildList(position, childCategoryList)
        }
    }

    private suspend inline fun <reified T> MarketGraphqlApi.getResponses(
        query: String?,
        queryName: String?,
        token: String? = ""
    ): Resource<ResponseBody<T>> {

        return try {
            Log.e(TAG, "Query: ${query?.getGraphqlApiBody()}")

            val result = this.callApi(token = "Token $token", body = query?.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG, "Token: $token")
            Log.e(TAG, "Query: $query")
            Log.e(TAG, "Response: ${result.body().toString()}")
            Log.e(TAG, "Response: ${result.code()}")
            when {
                result.isSuccessful -> {
                    Log.e(TAG, "Response: is successful")
                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else {
                        null
                    }
                    Log.e(TAG, "Response: 222  444444")
                    var json: T? = null
                    if (jsonObject.has("data")) {
                        Log.e(TAG, "Response: 222  ${jsonObject["data"]}")
                        var data = jsonObject["data"]
                        if (!error.isNullOrEmpty() && data.asJsonObject.has("sendMessage") || !error.isNullOrEmpty()) {
                            Log.e(TAG, "exceptionErrorRespnse " + error)
                        } else {
                            data = jsonObject["data"].asJsonObject
                            val queryResult = data[queryName]

                            json = when {
                                queryResult.isJsonArray -> {
                                    if (queryName.equals("feedItems")) {
                                        val listType =
                                            object : TypeToken<ArrayList<Products>>() {}.type
                                        Gson().fromJson(queryResult.asJsonArray, listType)
                                    } else if (queryName.equals("getAllCategories")) {
                                        val listType = object :
                                            TypeToken<ArrayList<GetAllCategoriesResponse>>() {}.type
                                        Gson().fromJson(queryResult.asJsonArray, listType)
                                    } else if (queryName.equals("getChildrenCategories")) {
                                        val listType = object :
                                            TypeToken<ArrayList<GetChildrenCategoriesResponse>>() {}.type
                                        Gson().fromJson(queryResult.asJsonArray, listType)
                                    } else if (queryName.equals("imageSearch")) {
                                        val listType =
                                            object : TypeToken<ArrayList<Products>>() {}.type
                                        Gson().fromJson(queryResult.asJsonArray, listType)
                                    } else if (queryName.equals("freightEstimate")) {
                                        val listType = object :
                                            TypeToken<ArrayList<FreightEstimateResponse>>() {}.type
                                        Gson().fromJson(queryResult.asJsonArray, listType)
                                    } else null
                                }

                                queryResult.isJsonObject -> Gson().fromJson(
                                    queryResult.asJsonObject,
                                    T::class.java
                                )

                                else -> null
                            }
                        }
                    }
                    Log.e(TAG, "Response: 222  7777")
                    val response = ResponseBody(data = json, errorMessage = error)
                    Log.e(TAG, "Response: $response")
                    Log.e(TAG, "Response: 222  55555")
                    if (response.errorMessage.isNullOrEmpty() && response.data != null) {
                        Log.e(TAG, "Response: 333")
                        Resource.Success(HttpStatusCode.OK, response)
                    } else {
                        Log.e(TAG, "Response: 444")
                        Log.e(TAG, response.errorMessage!!.toString())
                        if (response.errorMessage.contains("User does't exist")) {
//                            userPreferences1.clear()
                            val intent = Intent(requireActivity(), SplashActivity::class.java)
                            requireActivity().startActivity(intent)
                            (requireActivity() as Activity).finishAffinity()
                        }
                        Toast.makeText(
                            requireActivity(),
                            "User doesn't exist" + response.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        copyToClipboard(
                            requireActivity(),
                            "User doesn't exist" + response.errorMessage,
                            " User doesn't exist "
                        )
                        Resource.Error(
                            code = HttpStatusCode.BAD_REQUEST,
                            message = response.errorMessage
                        )
                    }
                }

                else -> {
                    Log.e("${TAG}VisitorMutation", "else: ${result}")
                    Toast.makeText(
                        requireActivity(),
                        "else--${result.code()}: ${result.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    copyToClipboard(
                        requireActivity(),
                        "else--${result.code()}: ${result.message()}",
                        " Else Error "
                    )
                    Resource.Error(
                        code = HttpStatusCode.INTERNAL_SERVER_ERROR,
                        message = App.getAppContext().getString(R.string.something_went_wrong)
                    )
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "Exception--${e.message}", Toast.LENGTH_SHORT)
                .show()
            copyToClipboard(requireActivity(), "Exception--${e.message}", " Exception Error ")
            e.printStackTrace()
            Resource.Error(
                code = HttpStatusCode.INTERNAL_SERVER_ERROR,
                message = "${
                    App.getAppContext().getString(R.string.something_went_wrong)
                } ${e.localizedMessage}"
            )
        }
    }
}
