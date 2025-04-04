package com.i69.ui.screens.main.coins

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.wallet.PaymentData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.i69.AvailableBokuOperatorsMutation
import com.i69.BuildConfig
import com.i69.ChargePaymentMutation
import com.i69.GetPaymentMethodsQuery
import com.i69.MobilePinInputMutation
import com.i69.OnPaymentMethodChangeSubscription
import com.i69.PaymentByOperationReferenceQuery
import com.i69.PaypalCapturePaymentMutation
import com.i69.PaypalCreateOrderMutation
import com.i69.PinAuthorisationMutation
import com.i69.R
import com.i69.R.id.ed_pin
import com.i69.StripeCreateIntentMutation
import com.i69.StripePaymentSuccessMutation
import com.i69.StripePublishableKeyQuery
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.billing.BillingDataSource
import com.i69.billing.BillingDataSource.Companion.IS_Payment_Done
import com.i69.billing.BillingDataSource.Companion.paypalCaptureId
import com.i69.data.remote.requests.PurchaseRequest
import com.i69.data.remote.responses.CoinPrice
import com.i69.databinding.FragmentPurchaseNewBinding
import com.i69.singleton.App
import com.i69.ui.adapters.AdapterCoinPrice
import com.i69.ui.adapters.OperatorsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.viewModels.PurchaseCoinsViewModel
import com.i69.utils.Resource
import com.i69.utils.SharedPref
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientSubscription
import com.i69.utils.autoSnackbarOnTop
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import com.paypal.checkout.paymentbutton.PayPalButton
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentIntent
import com.stripe.android.view.CardInputWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class PurchaseFragment : BaseFragment<FragmentPurchaseNewBinding>() {

    private var TAG: String = PurchaseFragment::class.java.simpleName
    val PAYPAL_REQUEST_CODE = 123
    private val viewModel: PurchaseCoinsViewModel by activityViewModels()
    private var selectedSku: Int = -1
    private var selectedCoins: Int = 0
    private var selectedPrice: Double = 0.0
    private lateinit var dialog: Dialog
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var paymentMeth = ""
    var latitude = ""
    var longtitude = ""
    private var coinPriceList = mutableListOf<CoinPrice>()
    private lateinit var adapterCoinPrice: AdapterCoinPrice
    var amount = 0.0
    var currentCurrency = ""
    lateinit var sharedPref: SharedPref
    lateinit var stripeBottomsheetDialog: BottomSheetDialog
    lateinit var paymentBottomsheetDialog: BottomSheetDialog

    lateinit var googlePay: MaterialCardView
    lateinit var google: MaterialCardView
    lateinit var boku: MaterialCardView
    lateinit var stripePay: MaterialCardView
    lateinit var payapal: MaterialCardView

    lateinit var paypalButton: PayPalButton

    lateinit var googlePayRadioButton: RadioButton
    lateinit var googleRadioButton: RadioButton
    lateinit var bokuRadioButton: RadioButton
    lateinit var stripeRadioButton: RadioButton
    lateinit var payapalRadioButton: RadioButton
    var paypalCreateOrderId = ""

    private var stripe: Stripe? = null
    private var paymentMethod = mutableListOf<GetPaymentMethodsQuery.GetPaymentMethod?>()

    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    var appStringConst: AppStringConstant? = null

    override fun getStatusBarColor() = R.color.colorPrimary
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPurchaseNewBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    override fun setupTheme() {
        navController = findNavController()

        viewStringConstModel.data.observe(this@PurchaseFragment) { data ->
            if (data != null) {
                binding?.stringConstant = data
                Log.e(TAG, "MyCoinsValueSetted : ${data.buy_coins_purchase}")
                appStringConst = data
            }
        }
        viewStringConstModel.data.also {
            if (it.value != null) {
                binding?.stringConstant = it.value
                appStringConst = it.value
            }
        }
        sharedPref = SharedPref(requireContext())
        getStripePublishableKey()
        Log.e(TAG, "BaseURL : ${BuildConfig.BASE_URL}")
        getPaymentMethods()
        onPaymentMethodChange()

        lifecycleScope.launch(Dispatchers.Main) {
            val token = getCurrentUserToken()!!
            viewModel.getCoinPrices(token) { error ->
                if (error == null) {
                    activity?.runOnUiThread {
                        coinPriceList.addAll(viewModel.coinPrice)
                        if (::adapterCoinPrice.isInitialized) {
                            adapterCoinPrice.notifyDataSetChanged()
                        }
                    }
                } else {

                }
            }
        }

        appStringConst?.let {
            adapterCoinPrice = AdapterCoinPrice(
                requireContext(),
                coinPriceList,
                it,
                object : AdapterCoinPrice.CoinPriceInterface {
                    override fun onClick(index: Int, coinPrice: CoinPrice) {
                        Log.e(
                            TAG,
                            "paymentCurrency" + "${coinPrice.currency} ${coinPrice.discountedPrice.toDouble()}" +
                                    "${coinPrice.coinsCount.toInt()}"
                        )
                        amount = coinPrice.discountedPrice.toDouble()
                        currentCurrency = coinPrice.currency
                        showBottomSheetForPaymentOptions(
                            index + 1,
                            coinPrice.coinsCount.toInt(),
                            coinPrice.discountedPrice.toDouble(),
                            coinPrice.currency
                        )
                        for (avc in paymentMethod) {

                            if (avc!!.paymentMethod.contentEquals("Google Pay")) {
                                if (avc.isAllowed) {
                                    googlePay.setViewVisible()
                                } else {
                                    googlePay.visibility = View.GONE
                                }
                            } else if (avc.paymentMethod.contentEquals("InApp")) {
                                if (avc.isAllowed) {
                                    google.setViewVisible()
                                } else {
                                    google.visibility = View.GONE
                                }
                            } else if (avc.paymentMethod.contentEquals("Paypal")) {
                                if (avc.isAllowed) {
                                    payapal.setViewVisible()
                                } else {
                                    payapal.visibility = View.GONE
                                }
                            } else if (avc.paymentMethod.contentEquals("Stripe")) {
                                if (avc.isAllowed) {
                                    stripePay.setViewVisible()
                                } else {
                                    stripePay.visibility = View.GONE
                                }
                            } else if (avc.paymentMethod.contentEquals("Boku")) {
                                if (avc.isAllowed) {
                                    boku.setViewVisible()
                                } else {
                                    boku.visibility = View.GONE
                                }
                            }
                        }
                    }
                })
            binding?.recyclerViewCoins?.adapter = adapterCoinPrice
            viewModel.consumedPurchase.observe(viewLifecycleOwner) { isPurchased ->
                isPurchased?.let {
                    Log.e(TAG, "Observing Purchased  $it")
                    showProgressView()
                    Log.e(TAG, "time_1: onPaymentSuccess")
                    if (it == 0) onPaymentSuccess("in-app-purchase", "$it")
                }
            }
        }
    }

    fun onPaymentMethodChange() {
        Log.e(TAG, "OnPaymentMethodChange....")
        lifecycleScope.launch(Dispatchers.IO) {
            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClientSubscription(requireContext(), userToken).subscription(
                    OnPaymentMethodChangeSubscription()
                ).toFlow().first()
            } catch (e: Exception) {
                Log.e(TAG, "Exception paymentChange: ${e.localizedMessage}")

                Log.e(TAG, "apolloResponse ${e.message}")
                if (isAdded) {
                    binding?.root?.snackbar("${getString(R.string.exception_get_payment_methods)}${e.message}")
                }
                hideProgressView()
                return@launch
            }

            if (response.hasErrors()) {
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG, "errorgetPaymentMethods : $errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {

                var i = 0
                if (paymentMethod.isNotEmpty()) {

                    for (avc in paymentMethod) {
                        i++
                        if (avc!!.paymentMethod.contentEquals(response.data!!.onPaymentMethodChange!!.paymentMethod)) {
                            break;
                        }
                    }
                }
                if (paymentMethod.isNotEmpty() && i in paymentMethod.indices) {
                    paymentMethod.removeAt(i)
                    var paymentData =
                        GetPaymentMethodsQuery.GetPaymentMethod(
                            isAllowed = response.data!!.onPaymentMethodChange!!.isAllowed!!,
                            paymentMethod = response.data!!.onPaymentMethodChange!!.paymentMethod!!
                        )
                    paymentMethod.add(i, paymentData)
                }


                if (::paymentBottomsheetDialog.isInitialized && paymentBottomsheetDialog.isShowing) {

                    for (avc in paymentMethod) {

                        if (avc!!.paymentMethod.contentEquals("Google Pay")) {
                            if (avc.isAllowed) {
                                googlePay.setViewVisible()
                            } else {
                                googlePay.visibility = View.GONE
                            }
                        } else if (avc.paymentMethod.contentEquals("InApp")) {
                            if (avc.isAllowed) {
                                google.setViewVisible()
                            } else {
                                google.visibility = View.GONE
                            }
                        } else if (avc.paymentMethod.contentEquals("Paypal")) {
                            if (avc.isAllowed) {
                                payapal.visibility = View.VISIBLE
                            } else {
                                payapal.visibility = View.GONE
                            }
                        } else if (avc.paymentMethod.contentEquals("Stripe")) {
                            if (avc.isAllowed) {
                                stripePay.visibility = View.VISIBLE
                            } else {
                                stripePay.visibility = View.GONE
                            }
                        } else if (avc.paymentMethod.contentEquals("Boku")) {
                            if (avc.isAllowed) {
                                boku.visibility = View.VISIBLE
                            } else {
                                boku.visibility = View.GONE
                            }
                        }
                    }

                    if (googlePay.visibility == View.GONE) {
                        if (boku.visibility == View.GONE) {
                            if (stripePay.visibility == View.GONE) {
                                if (payapal.visibility == View.GONE) {
                                    googlePayRadioButton.isChecked = false
                                    googleRadioButton.isChecked = false
                                    googleRadioButton.isChecked = false
                                    bokuRadioButton.isChecked = false
                                    stripeRadioButton.isChecked = false
                                    payapalRadioButton.isChecked = false
                                } else {
                                    googlePayRadioButton.isChecked = false
                                    googleRadioButton.isChecked = false
                                    bokuRadioButton.isChecked = false
                                    stripeRadioButton.isChecked = false
                                    payapalRadioButton.isChecked = true
                                }
                            } else {
                                googlePayRadioButton.isChecked = false
                                googleRadioButton.isChecked = false
                                bokuRadioButton.isChecked = false
                                stripeRadioButton.isChecked = true
                                payapalRadioButton.isChecked = false
                            }
                        } else {
                            googlePayRadioButton.isChecked = false
                            googleRadioButton.isChecked = false
                            bokuRadioButton.isChecked = true
                            stripeRadioButton.isChecked = false
                            payapalRadioButton.isChecked = false
                        }
                    } else {
                        googlePayRadioButton.isChecked = true
                        bokuRadioButton.isChecked = false
                        stripeRadioButton.isChecked = false
                        payapalRadioButton.isChecked = false
                    }
                }
                Log.e(TAG, "onPaymentUpdate : ${Gson().toJson(response)}")
            }
        }
    }

    fun getPaymentMethods() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val userToken = getCurrentUserToken()!!
                val response = try {
                    apolloClient(requireContext(), userToken).query(
                        GetPaymentMethodsQuery()
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    binding?.root?.snackbar("${getString(R.string.exception_get_payment_methods)}${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
                }

                if (response.hasErrors()) {
                    val errorMessage = response.errors?.get(0)?.message
                    Log.e(TAG, "errorgetPaymentMethods : $errorMessage")

                    if (errorMessage != null) {
                        binding?.root?.snackbar(errorMessage)
                    }
                } else {
                    response.data!!.getPaymentMethods?.let { paymentMethod.addAll(it) }
                    Log.e(TAG, "getPaymentMethodsRespns " + "${Gson().toJson(response)}")
                }
            }
        }
    }

    fun getStripePublishableKey() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val userToken = getCurrentUserToken()!!
                val response = try {
                    apolloClient(requireContext(), userToken).query(
                        StripePublishableKeyQuery()
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    return@repeatOnLifecycle
                } finally {
                    hideProgressView()
                }

                if (response.hasErrors()) {
                    val errorMessage = response.errors?.get(0)?.message
                    Log.e(TAG, "errorInstripePublishkey : $errorMessage")

                    if (errorMessage != null) {
                        binding?.root?.snackbar(errorMessage)
                    }
                } else {
                    App.initStripe(
                        response.data!!.stripePublishableKey!!.publishableKey!!,
                        this@PurchaseFragment
                    )
                    Log.e(TAG, "getStripePublishKey : " + Gson().toJson(response))
                    lifecycleScope.launch(Dispatchers.Main) {

                        stripe = Stripe(
                            requireContext(),
                            PaymentConfiguration.getInstance(requireContext()).publishableKey
                        )
                        MainActivity.getMainActivity()!!.setStripePayMentIntent(stripe!!)
                    }
                }
            }
        }
    }

    private fun statusCheck() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGPS()
        }
    }

    private fun buildAlertMessageNoGPS() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setMessage("Your GPS seems to be disabled, do you want to enable it ?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { _, _ -> startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { p0, _ -> p0?.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetForPaymentOptions(
        sku: Int,
        coins: Int,
        buyingPrice: Double,
        coinCurrency: String
    ) {
        paymentBottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_payment_options_new, null, false)
        googlePay = customView.findViewById<MaterialCardView>(R.id.cd_googlepay)
        google = customView.findViewById<MaterialCardView>(R.id.cd_google)
        boku = customView.findViewById<MaterialCardView>(R.id.cd_boku)
        stripePay = customView.findViewById<MaterialCardView>(R.id.cd_stripe)
        payapal = customView.findViewById<MaterialCardView>(R.id.cd_paypal)
        googlePayRadioButton = customView.findViewById<RadioButton>(R.id.rb_gpay)
        googleRadioButton = customView.findViewById<RadioButton>(R.id.rb_gOOGLE)
        bokuRadioButton = customView.findViewById<RadioButton>(R.id.rb_boku)
        stripeRadioButton = customView.findViewById<RadioButton>(R.id.rb_stripe)
        payapalRadioButton = customView.findViewById<RadioButton>(R.id.rb_paypal)
        val proceedToPayment = customView.findViewById<MaterialCardView>(R.id.cd_proceedtopayment)
        for (avc in paymentMethod) {
            if (avc!!.paymentMethod.contentEquals("Google Pay")) {
                if (avc.isAllowed) {
                    googlePay.setViewVisible()
                } else {
                    googlePay.visibility = View.GONE
                }
            } else if (avc.paymentMethod.contentEquals("InApp")) {
                if (avc.isAllowed) {
                    google.setViewVisible()
                } else {
                    google.visibility = View.GONE
                }
            } else if (avc.paymentMethod.contentEquals("Paypal")) {
                if (avc.isAllowed) {
                    payapal.visibility = View.VISIBLE
                } else {
                    payapal.visibility = View.GONE
                }
            } else if (avc.paymentMethod.contentEquals("Stripe")) {
                if (avc.isAllowed) {
                    stripePay.visibility = View.VISIBLE
                } else {
                    stripePay.visibility = View.GONE
                }
            } else if (avc.paymentMethod.contentEquals("Boku")) {
                if (avc.isAllowed) {
                    boku.visibility = View.VISIBLE
                } else {
                    boku.visibility = View.GONE
                }
            }
        }

        if (googlePay.visibility == View.GONE) {
            if (boku.visibility == View.GONE) {
                if (stripePay.visibility == View.GONE) {
                    if (payapal.visibility == View.GONE) {
                        googlePayRadioButton.isChecked = false
                        googleRadioButton.isChecked = false
                        bokuRadioButton.isChecked = false
                        stripeRadioButton.isChecked = false
                        payapalRadioButton.isChecked = false
                    } else {
                        googlePayRadioButton.isChecked = false
                        googleRadioButton.isChecked = false
                        bokuRadioButton.isChecked = false
                        stripeRadioButton.isChecked = false
                        payapalRadioButton.isChecked = true
                    }
                } else {
                    googlePayRadioButton.isChecked = false
                    googleRadioButton.isChecked = false
                    bokuRadioButton.isChecked = false
                    stripeRadioButton.isChecked = true
                    payapalRadioButton.isChecked = false
                }
            } else {
                googlePayRadioButton.isChecked = false
                googleRadioButton.isChecked = false
                bokuRadioButton.isChecked = true
                stripeRadioButton.isChecked = false
                payapalRadioButton.isChecked = false
            }
        } else {
            googlePayRadioButton.isChecked = true
            bokuRadioButton.isChecked = false
            stripeRadioButton.isChecked = false
            payapalRadioButton.isChecked = false
        }
        googlePay.setOnClickListener {
            googlePayRadioButton.isChecked = true
            googleRadioButton.isChecked = false
            bokuRadioButton.isChecked = false
            stripeRadioButton.isChecked = false
            payapalRadioButton.isChecked = false
        }
        google.setOnClickListener {
            googleRadioButton.isChecked = true
            googlePayRadioButton.isChecked = false
            bokuRadioButton.isChecked = false
            stripeRadioButton.isChecked = false
            payapalRadioButton.isChecked = false
        }

        boku.setOnClickListener {
            googlePayRadioButton.isChecked = false
            googleRadioButton.isChecked = false
            bokuRadioButton.isChecked = true
            stripeRadioButton.isChecked = false
            payapalRadioButton.isChecked = false
        }

        stripePay.setOnClickListener {
            googlePayRadioButton.isChecked = false
            googleRadioButton.isChecked = false
            bokuRadioButton.isChecked = false
            stripeRadioButton.isChecked = true
            payapalRadioButton.isChecked = false
        }

        payapal.setOnClickListener {
            googlePayRadioButton.isChecked = false
            googleRadioButton.isChecked = false
            bokuRadioButton.isChecked = false
            stripeRadioButton.isChecked = false
            payapalRadioButton.isChecked = true
        }

        proceedToPayment.setOnClickListener {

            if (stripeRadioButton.isChecked) {
                selectedSku = sku
                selectedCoins = coins
                selectedPrice = buyingPrice
                paymentMeth = "stripe"
                startStripePayment(coins, buyingPrice, coinCurrency)
                paymentBottomsheetDialog.dismiss()
            } else if (googlePayRadioButton.isChecked) {
                Log.e(TAG, "InAppPurchase : showPrice:Data ")
                selectedSku = sku
                selectedCoins = coins
                selectedPrice = buyingPrice
                paymentMeth = "Google Pay"
                requestPayment()
                paymentBottomsheetDialog.dismiss()

            } else if (googleRadioButton.isChecked) {
                Log.e(TAG, "InAppPurchase : showPrice:Data ")
                selectedSku = sku
                selectedCoins = coins
                selectedPrice = buyingPrice
                paymentMeth = "InApp"
                val product = getSkuProductById(selectedSku)
                Log.e(TAG, "InAppPurchase : InAppPurchaseproduct  :  $product")
                makePurchase(product)
                paymentBottomsheetDialog.dismiss()

            } else if (bokuRadioButton.isChecked) {
                Log.e(TAG, "InAppPurchase : Latitude $latitude Longitude $longtitude")
                if (latitude.isNotEmpty()) {
                    paymentMeth = "Boku"
                    startBokuPayment()
                    paymentBottomsheetDialog.dismiss()
                } else {
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
                            if (location != null) {
                                latitude = location.latitude.toString()
                                longtitude = location.longitude.toString()
                                paymentMeth = "Boku"
                                startBokuPayment()
                                paymentBottomsheetDialog.dismiss()
                            } else {
                                statusCheck()
                            }
                        }
                    }

                }
            } else if (payapalRadioButton.isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedSku = sku
                    selectedCoins = coins
                    selectedPrice = buyingPrice
                    paymentMeth = "paypal"
                    paypalcreateOrder(amount, currentCurrency)

                } else {
                    Toast.makeText(
                        activity,
                        "Checkout SDK only available for API 23+",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Checkout SDK only available for API 23+",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        paymentBottomsheetDialog.setContentView(customView)
        paymentBottomsheetDialog.show()

        paymentBottomsheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        paymentBottomsheetDialog.dismissWithAnimation = true
        paymentBottomsheetDialog.behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        paymentBottomsheetDialog.dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun startBokuPayment() {
        val bottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_operators_list, null, false)
        val recyclerViewOperators = customView.findViewById<RecyclerView>(R.id.rv_operators)
        val textviewNoOperators =
            customView.findViewById<MaterialTextView>(R.id.tv_nooperatorsavailable)
        val progressBar = customView.findViewById<ProgressBar>(R.id.progressbar)
        val operatorAdapter = OperatorsAdapter()
        recyclerViewOperators.adapter = operatorAdapter
        recyclerViewOperators.layoutManager = LinearLayoutManager(requireContext())
        operatorAdapter.setOnItemClickListener {
            sharedPref.setOperatorCode(it.toString())
            startPinAuthorisation()
            bottomsheetDialog.dismiss()
        }
        lifecycleScope.launch {
            val userToken = getCurrentUserToken()
            try {
                val response = apolloClient(requireContext(), userToken!!).mutation(
                    AvailableBokuOperatorsMutation(latitude, longtitude)
                ).execute()
                Log.e(TAG, "csdcdscc " + response.toString())
                progressBar.visibility = View.GONE
                if (response.hasErrors()) {
                    textviewNoOperators.visibility = View.VISIBLE
                    textviewNoOperators.text = response.errors?.get(0)?.message
                        ?: AppStringConstant1.something_went_wrong_please_try_again_later
                } else {
                    val operators: List<String?> =
                        response.data?.availableBokuOperators?.operators ?: listOf()
                    Log.e(TAG, "PurchaseFragment : Operators $operators")
                    if (operators.isNotEmpty()) {
                        operatorAdapter.operatorList = operators
                    } else {
                        textviewNoOperators.visibility = View.VISIBLE
                    }
                }
            } catch (e: ApolloException) {
                Log.e(TAG, "Operators Exception ${e.message}")
                progressBar.visibility = View.GONE
                textviewNoOperators.text = e.message
            } catch (e: Exception) {
                Log.e(TAG, "Operators Exception ${e.message}")
                progressBar.visibility = View.GONE
                textviewNoOperators.text = e.message
            }
        }
        bottomsheetDialog.setContentView(customView)
        bottomsheetDialog.show()
    }

    private fun startPinAuthorisation() {
        val bottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_entermobilenumber, null, false)
        val ediTextMobileNumber = customView.findViewById<TextInputEditText>(R.id.ed_mobile)
        val submitCardView = customView.findViewById<MaterialCardView>(R.id.cd_submit)
        val textInputLayout = customView.findViewById<TextInputLayout>(R.id.tl_mobile)
        val title = customView.findViewById<MaterialTextView>(R.id.description)
        val progressbar = customView.findViewById<ProgressBar>(R.id.progressbar)
        val operatorCode = sharedPref.getOperatorCode()
        submitCardView.setOnClickListener {
            val mobileNumber = ediTextMobileNumber.text.toString()
            if (mobileNumber.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    AppStringConstant1.please_enter_mobile_number,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            textInputLayout.visibility = View.GONE
            title.text = AppStringConstant1.please_wait_sending_the_pin
            submitCardView.visibility = View.GONE
            lifecycleScope.launch {
                val userToken = getCurrentUserToken()
                val userId = getCurrentUserId()
                try {
                    val response = apolloClient(requireContext(), userToken!!).mutation(
                        PinAuthorisationMutation(
                            mobileNumber,
                            operatorCode,
                            false,
                            userId.toString()
                        )
                    ).execute()
                    if (response.hasErrors()) {
                        bottomsheetDialog.dismiss()
                        binding?.root?.snackbar(response.errors?.get(0)?.message.toString())
                    } else {
                        val data = response.data?.pinAuthorisation
                        if (data?.success == true) {
                            sharedPref.setOperatorCode(data.operationReference)
                            verifyPIN()
                        } else {
                            bottomsheetDialog.dismiss()
                            binding?.root?.snackbar(AppStringConstant1.sorry_pin_failed_to_send)
                        }
                    }
                } catch (e: ApolloException) {
                    bottomsheetDialog.dismiss()
                    binding?.root?.snackbar("${e.message}")
                } catch (e: Exception) {
                    bottomsheetDialog.dismiss()
                    binding?.root?.snackbar("${e.message}")
                }
            }
        }
        bottomsheetDialog.setContentView(customView)
        bottomsheetDialog.show()
    }


    fun paymentIntentComplete(paymentResult: PaymentIntent) {
        Log.e(TAG, "PaymentResponseSuccess: " + paymentResult.paymentMethodId!!)
        Log.e(TAG, "PaymentResponseSuccess: " + paymentResult.id!!)
        showProgressView()
        Log.e(TAG, "PaymentResponseSuccess: " + "StripeData 6")

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                val userToken = getCurrentUserToken()!!
                val response = try {
                    Log.e(TAG, "PaymentResponseSuccess: " + "StripeData 5")

                    apolloClient(requireContext(), userToken).mutation(
                        StripePaymentSuccessMutation(
                            paymentResult.id!!,
                            paymentResult.status!!.code
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloException ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    hideProgressView()
                    Log.e(TAG, "PaymentResponseSuccess: " + "StripeData 8")

                    return@repeatOnLifecycle
                }

                if (response.hasErrors()) {
                    hideProgressView()
                    Log.e(TAG, "PaymentResponseSuccess: " + "StripeData 7")

                    val errorMessage = response.errors?.get(0)?.message
                    Log.e(TAG, "errorCreateIntent: $errorMessage")
                    if (errorMessage != null) {
                        binding?.root?.snackbar(errorMessage)
                    }
                } else {

                    hideProgressView()
                    Log.e(TAG, "StripeData 9")
                    stripeBottomsheetDialog.dismiss()
                    Log.e(TAG, Gson().toJson(response))
                    onPaymentSuccess("in-app-purchase", paymentResult.id!!)
                }
            }
        }
    }

    private fun startStripePayment(coins: Int, buyingPrice: Double, coinCurrency: String) {

        stripeBottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_stripe_detail, null, false)
        val stripeCardInput = customView.findViewById<CardInputWidget>(R.id.stripeCardInput)
        val submitCardView = customView.findViewById<MaterialCardView>(R.id.cd_submit)
        submitCardView.setOnClickListener {
            stripeCardInput.paymentMethodCreateParams?.let { params ->
                showProgressView()
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        val userToken = getCurrentUserToken()!!
                        val response = try {
                            Log.e(TAG, "StripeData ")
                            apolloClient(requireContext(), userToken).mutation(
                                StripeCreateIntentMutation(buyingPrice, coinCurrency, "card")
                            ).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloException ${e.message}")
                            Log.e(TAG, "StripeData 1")
                            binding?.root?.snackbar("${e.message}")
                            hideProgressView()
                            Log.e(TAG, "StripeData 3")
                            return@repeatOnLifecycle
                        }

                        if (response.hasErrors()) {
                            hideProgressView()
                            Log.e(TAG, "StripeData 2")
                            val errorMessage = response.errors?.get(0)?.message
                            Log.e(TAG, "errorCreateIntent: " + "$errorMessage")
                            if (errorMessage != null) {
                                binding?.root?.snackbar(errorMessage)
                            }
                        } else {
                            hideProgressView()

                            Log.e(TAG, "StripeData 4")

                            MainActivity.getMainActivity()!!.showLoader();

                            val clientSecret = response.data!!.stripeCreateIntent!!.clientSecret!!
                            Log.e(TAG, "StripeCreateIntent: ${Gson().toJson(response)}")
                            lifecycleScope.launch {
                                val confirmParams = ConfirmPaymentIntentParams
                                    .createWithPaymentMethodCreateParams(params, clientSecret)
                                stripe?.confirmPayment(
                                    MainActivity.getMainActivity()!!,
                                    confirmParams
                                )
                            }
                        }
                    }
                }
            }
        }
        stripeBottomsheetDialog.setContentView(customView)
        stripeBottomsheetDialog.show()
    }

    private fun verifyPIN() {
        val bottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_enterpin, null, false).apply {
        }

        val submitCardView = customView.findViewById<MaterialCardView>(R.id.cd_submit)
        val textInputLayout = customView.findViewById<TextInputLayout>(R.id.tl_mobile)
        val ediTextPIN = customView.findViewById<TextInputEditText>(ed_pin)
        val title = customView.findViewById<MaterialTextView>(R.id.description)
        val progressbar = customView.findViewById<ProgressBar>(R.id.progressbar)
        val operationReference = sharedPref.getOperatorReference()
        var userToken: String? = ""
        var chargingToken = ""
        lifecycleScope.launch {
            try {
                userToken = getCurrentUserToken()
                val response = apolloClient(requireContext(), userToken!!).query(
                    PaymentByOperationReferenceQuery(operationReference)
                ).execute()
                if (response.hasErrors()) {
                    bottomsheetDialog.show()
                    binding?.root?.snackbar(
                        "${AppStringConstant1.charging_token_exception} ${
                            response.errors?.get(
                                0
                            )?.message
                        }"
                    )
                } else {
                    chargingToken =
                        response.data?.paymentByOperationReference?.chargingToken.toString()
                }
            } catch (e: ApolloException) {
                bottomsheetDialog.show()
                binding?.root?.snackbar("${AppStringConstant1.charging_token_exception} ${e.message}")
            } catch (e: Exception) {
                bottomsheetDialog.show()
                binding?.root?.snackbar("${AppStringConstant1.charging_token_exception} ${e.message}")
            }
        }
        submitCardView.setOnClickListener {
            val pin = ediTextPIN.text.toString()
            if (pin.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    AppStringConstant1.please_enter_pin,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            textInputLayout.visibility = View.GONE
            title.text =
                AppStringConstant1.please_wait_verifying_the_pin
            submitCardView.visibility = View.GONE
            lifecycleScope.launch {
                try {
                    val response = apolloClient(requireContext(), userToken!!).mutation(
                        MobilePinInputMutation(chargingToken, pin.toInt())
                    ).execute()
                    if (response.hasErrors()) {
                        bottomsheetDialog.show()

                        binding?.root?.snackbar(
                            "${AppStringConstant1.pin_verification_successful_please_wait} ${
                                response.errors?.get(
                                    0
                                )?.message
                            }"
                        )
                    } else {
                        title.text =
                            AppStringConstant1.pin_verification_successful_please_wait
                        try {
                            val responseForPaymentOperation =
                                apolloClient(requireContext(), userToken!!).query(
                                    PaymentByOperationReferenceQuery(operationReference)
                                ).execute()
                            if (responseForPaymentOperation.hasErrors()) {
                                bottomsheetDialog.show()
                                binding?.root?.snackbar(
                                    "${AppStringConstant1.authorisation_exception} ${
                                        responseForPaymentOperation.errors?.get(
                                            0
                                        )?.message
                                    }"
                                )
                            } else {
                                when (responseForPaymentOperation.data?.paymentByOperationReference?.authorisationState) {
                                    "verified" -> {
                                        title.text =
                                            AppStringConstant1.fetching_charging_token_please_wait
                                        try {
                                            val responseForChargingToken =
                                                apolloClient(requireContext(), userToken!!).query(
                                                    PaymentByOperationReferenceQuery(
                                                        operationReference
                                                    )
                                                ).execute()
                                            if (responseForChargingToken.hasErrors()) {
                                                bottomsheetDialog.show()

                                                binding?.root?.snackbar(
                                                    "${AppStringConstant1.charging_token_exception} ${
                                                        responseForChargingToken.errors?.get(
                                                            0
                                                        )?.message
                                                    }"
                                                )

                                            } else {
                                                val newChargingToken =
                                                    responseForChargingToken.data?.paymentByOperationReference?.chargingToken.toString()
                                                title.text =
                                                    AppStringConstant1.charging_payment_please_wait
                                                try {
                                                    val responseFromChargePayment = apolloClient(
                                                        requireContext(),
                                                        userToken!!
                                                    ).mutation(
                                                        ChargePaymentMutation(
                                                            amount,
                                                            newChargingToken,
                                                            "You bought coins from i69 app"
                                                        )
                                                    ).execute()
                                                    if (responseFromChargePayment.hasErrors()) {
                                                        bottomsheetDialog.show()


                                                        binding?.root?.snackbar(
                                                            "${AppStringConstant1.charging_payment_exception} ${
                                                                responseFromChargePayment.errors?.get(
                                                                    0
                                                                )?.message
                                                            }"
                                                        )
                                                    } else {
                                                        val status =
                                                            responseFromChargePayment?.data?.chargePayment?.success
                                                        if (status == true) {

                                                            onPaymentSuccess(
                                                                "in-app-purchase",
                                                                "${responseFromChargePayment.data?.chargePayment!!.id}"
                                                            )
                                                            bottomsheetDialog.show()
                                                            binding?.root?.snackbar(
                                                                AppStringConstant1.you_successfuly_bought_the_coins
                                                            )
                                                        } else {
                                                            bottomsheetDialog.show()
                                                            binding?.root?.snackbar(
                                                                "${AppStringConstant1.charging_payment_exception} ${
                                                                    responseFromChargePayment.errors?.get(
                                                                        0
                                                                    )?.message
                                                                }"
                                                            )
                                                        }
                                                    }
                                                } catch (e: ApolloException) {
                                                    bottomsheetDialog.show()
                                                    binding?.root?.snackbar("${AppStringConstant1.charging_payment_exception} ${e.message}")
                                                } catch (e: Exception) {
                                                    bottomsheetDialog.show()
                                                    binding?.root?.snackbar("${AppStringConstant1.charging_payment_exception} ${e.message}")
                                                }
                                            }
                                        } catch (e: ApolloException) {
                                            bottomsheetDialog.show()
                                            binding?.root?.snackbar("${AppStringConstant1.charging_token_exception} ${e.message}")
                                        } catch (e: Exception) {
                                            bottomsheetDialog.show()
                                            binding?.root?.snackbar("${AppStringConstant1.charging_token_exception} ${e.message}")
                                        }
                                    }

                                    else -> {
                                        bottomsheetDialog.show()
                                        binding?.root?.snackbar("${AppStringConstant1.authorisation} ${responseForPaymentOperation.data?.paymentByOperationReference?.authorisationState}")
                                    }

                                }
                            }
                        } catch (e: ApolloException) {
                            bottomsheetDialog.show()
                            binding?.root?.snackbar("${AppStringConstant1.authorisation_exception} ${e.message}")
                        } catch (e: Exception) {
                            bottomsheetDialog.show()
                            binding?.root?.snackbar("${AppStringConstant1.authorisation_exception} ${e.message}")
                        }
                    }
                } catch (e: ApolloException) {
                    bottomsheetDialog.show()
                    binding?.root?.snackbar("${AppStringConstant1.pin_verification_exception} ${e.message}")
                } catch (e: Exception) {
                    bottomsheetDialog.show()
                    binding?.root?.snackbar("${AppStringConstant1.pin_verification_exception} ${e.message}")
                }
            }
        }
        bottomsheetDialog.setContentView(customView)
        bottomsheetDialog.show()
    }

    override fun setupClickListeners() {
        binding?.purchaseClose?.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun getSkuProductById(sku: Int) = when (sku) {
        1 -> com.i69.data.config.Constants.IN_APP_FIRST_TYPE
        2 -> com.i69.data.config.Constants.IN_APP_SECOND_TYPE
        3 -> com.i69.data.config.Constants.IN_APP_THIRD_TYPE
        4 -> com.i69.data.config.Constants.IN_APP_FOURTH_TYPE
        5 -> com.i69.data.config.Constants.IN_APP_FIFTH_TYPE
        6 -> com.i69.data.config.Constants.IN_APP_SIXTH_TYPE
        7 -> com.i69.data.config.Constants.IN_APP_SEVENTH_TYPE
        8 -> com.i69.data.config.Constants.IN_APP_EIGHTH_TYPE
        9 -> com.i69.data.config.Constants.IN_APP_NINETH_TYPE
        10 -> com.i69.data.config.Constants.IN_APP_TENTH_TYPE
        else -> com.i69.data.config.Constants.IN_APP_FIRST_TYPE
    }

    private fun makePurchase(sku: String) {
        viewModel.buySku(requireActivity(), sku)
    }


    private fun paypalcreateOrder(amount: Double, currency: String) {
        lifecycleScope.launch {
            val userToken = getCurrentUserToken()
            try {
                val response = apolloClient(requireContext(), userToken!!).mutation(
                    PaypalCreateOrderMutation(amount, currency)

                ).execute()
                Log.e(TAG, "ddsfsd: " + response.toString())

                if (response.hasErrors()) {
                    Log.e(TAG, "ascdsvsd" + response.errors?.get(0)?.message.toString())

                    response.errors?.get(0)?.message
                        ?: getString(R.string.something_went_wrong_please_try_again_later)

                    Toast.makeText(
                        activity,
                        getString(R.string.something_went_wrong_please_try_again_later),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    paypalCreateOrderId = response.data?.paypalCreateOrder?.id.toString()

                    val intent = Intent(context, WebPaymentActivity::class.java)
                    intent.putExtra(
                        "url",
                        "https://www.paypal.com/checkoutnow?token=$paypalCreateOrderId"
                    )
                    intent.putExtra("id", paypalCreateOrderId)
                    startActivity(intent)
                    paymentBottomsheetDialog.dismiss()

                    Log.e(
                        TAG,
                        "CreatePaypalOrderId: " + Gson().toJson(response.data?.paypalCreateOrder)
                    )

                }
            } catch (e: ApolloException) {
                Log.e(TAG, "PurchaseFragment : " + "Operators Exception ${e.message}")

            } catch (e: Exception) {
                Log.e(TAG, "PurchaseFragment" + "Operators Exception ${e.message}")

            }
        }
    }


    private fun paypalCapturePayment(orderId: String) {

        Log.e(TAG, "craetedOrderId : $paypalCreateOrderId")
        Log.e(TAG, "FromcraetedOrderId: ")
        lifecycleScope.launch {
            val userToken = getCurrentUserToken()
            try {
                val response = apolloClient(requireContext(), userToken!!).mutation(
                    PaypalCapturePaymentMutation(paypalCreateOrderId)
                ).execute()

                if (response.hasErrors()) {

                    var message = response.errors?.get(0)?.message
                        ?: AppStringConstant1.something_went_wrong_please_try_again_later
//                        ?: "Something went wrong, Please try after sometime"

                    Log.e(TAG, "MyPaymentIdWrong" + Gson().toJson(response.errors))
                    binding?.root?.snackbar(message)
                } else {

                    Log.e(
                        TAG,
                        "CapturePaypalOrderId" +
                                Gson().toJson(response.data?.paypalCapturePayment)
                    )
                    onPaymentSuccess("in-app-purchase", response.data?.paypalCapturePayment?.id!!)
                }
            } catch (e: ApolloException) {
                Log.e(TAG, "PurchaseFragment" + "Operators Exception ${e.message}")

            } catch (e: Exception) {
                Log.e(TAG, "PurchaseFragment" + "Operators Exception ${e.message}")

            }
        }
    }


    public fun onPaymentSuccess(method: String, paymentId: String) {
        showProgressView()

        lifecycleScope.launch(Dispatchers.Main) {
            val userId = getCurrentUserId()!!
            val userToken = getCurrentUserToken()!!
            val purchaseRequest = PurchaseRequest(
                id = userId,
                currency = currentCurrency,
                method = method,
                coins = selectedCoins,
                money = selectedPrice,
                paymentMethod = paymentMeth,
                payment_id = paymentId
            )

            Log.e(TAG, "MyPaymentId: $paymentId")
            when (val response = viewModel.purchaseCoin(purchaseRequest, token = userToken)) {
                is Resource.Success -> {
                    Log.e(TAG, "StripeData 10")
                    if (response.data?.data!!.success) {
                        Log.e(TAG, "myPurchaseCoinResponce" + Gson().toJson(response))
                        viewModel.loadCurrentUser(userId, token = userToken, true)
                        hideProgressView()

                        val successMsg =
                            String.format(AppStringConstant1.congrats_purchase, selectedCoins)
                        binding?.root?.autoSnackbarOnTop(successMsg, Snackbar.LENGTH_LONG) {
                            moveToProfileScreen()
                        }
                    } else {
                        Log.e(TAG, "myPurchaseCoinResponce : " + Gson().toJson(response))
                        hideProgressView()
                        onFailureListener(AppStringConstant1.something_went_wrong)

                    }
                }

                is Resource.Error -> onFailureListener(
                    response.message ?: ""
                )

                else -> {
                    Log.e(TAG, "StripeData 12")
                }
            }
        }
    }

    private fun moveToProfileScreen() {
        try {
            navController?.navigate(R.id.action_purchaseFragment_to_userProfileFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public fun onGooglePaymentSuccess(method: String, paymentId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val userId = getCurrentUserId()!!
            val userToken = getCurrentUserToken()!!
            val purchaseRequest = PurchaseRequest(
                id = userId,
                currency = currentCurrency,
                method = method,
                coins = selectedCoins,
                money = selectedPrice,
                paymentMethod = paymentMeth,
                payment_id = paymentId
            )

            Log.e(TAG, "MyPaymentId: $paymentId")
            when (val response = viewModel.googleCreateOrder(purchaseRequest, token = userToken)) {
                is Resource.Success -> {
                    hideProgressView()
                    if (response.data?.data!!.success) {
                        Log.e(TAG, "myPurchaseCoinResponce : ${Gson().toJson(response)}")
                    } else {
                        Log.e(TAG, "myPurchaseCoinResponce: " + Gson().toJson(response))
                        onFailureListener(AppStringConstant1.something_went_wrong)
                    }
                }

                is Resource.Error -> onFailureListener(
                    response.message ?: ""
                )

                else -> {}
            }
        }
    }

    private fun congratulationsToast(coins: Int) {
        binding?.root?.snackbar(String.format(AppStringConstant1.congrats_purchase, coins))
    }

    private fun onFailureListener(error: String) {
        hideProgressView()
        Log.e(TAG, "StripeData 11")

        Log.e(TAG, "${getString(R.string.something_went_wrong)} $error")
        binding?.root?.snackbar("${AppStringConstant1.something_went_wrong} $error")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        if (BillingDataSource.purchasesUpdated) {
            showProgressView()
            BillingDataSource.purchasesUpdated = false
        }

        if (WebPaymentActivity.IS_Done) {
            Log.e(TAG, "resume: " + WebPaymentActivity.paypalCapturePayment)
            onPaymentSuccess("in-app-purchase", WebPaymentActivity.paypalCapturePayment)
            WebPaymentActivity.IS_Done = false
        } else if (IS_Payment_Done) {
            Log.e(TAG, "resume: $paypalCaptureId")
            Toast.makeText(requireContext(), "Coins Updated", Toast.LENGTH_SHORT).show()
            onPaymentSuccess("COINS", paypalCaptureId)
            IS_Payment_Done = false
        }
    }

    private fun requestPayment() {
        val dummyPriceCents = 100L
        val shippingCostCents = 900L
        val task = viewModel.getLoadPaymentDataTask(dummyPriceCents + shippingCostCents)

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }

                    is ApiException -> {
                        handleError(exception.statusCode, exception.message)
                    }

                    else -> {
                        handleError(
                            CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                    " exception when trying to deliver the task result to an activity!"
                        )
                    }
                }
            }
        }
    }

    private val resolvePaymentForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK ->
                    result.data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                    }

                RESULT_CANCELED -> {
                    // The user cancelled the payment attempt
                }
            }
        }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [Payment
     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.e(TAG, "BillingName: $billingName")
            Log.e(TAG, "paymentMethodData" + paymentMethodData.toString())
            Log.e(
                TAG,
                "Google Pay token" + paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )

            onPaymentSuccess(
                "in-app-purchase", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )
            onGooglePaymentSuccess(
                "in-app-purchase", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )
        } catch (error: JSONException) {
            Log.e(TAG, "handlePaymentSuccess" + "Error: $error")
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e(TAG, "Google Pay API error" + "Error code: $statusCode, Message: $message")
    }


}
