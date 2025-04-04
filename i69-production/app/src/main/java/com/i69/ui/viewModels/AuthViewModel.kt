package com.i69.ui.viewModels

import android.app.Activity
import android.content.Context
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.i69.GetAllSocialAuthStatusQuery
import com.i69.R
import com.i69.data.enums.LoginProvider
import com.i69.data.models.Id
import com.i69.data.models.Photo
import com.i69.data.models.User
import com.i69.data.preferences.UserPreferences
import com.i69.data.remote.repository.AppRepository
import com.i69.data.remote.repository.CoinRepository
import com.i69.data.remote.repository.LoginRepository
import com.i69.data.remote.repository.UserDetailsRepository
import com.i69.data.remote.repository.UserUpdateRepository
import com.i69.data.remote.requests.LoginRequest
import com.i69.data.remote.responses.CoinsResponse
import com.i69.data.remote.responses.DefaultPicker
import com.i69.data.remote.responses.LoginResponse
import com.i69.data.remote.responses.ResponseBody
import com.i69.firebasenotification.FCMHandler
import com.i69.singleton.App
import com.i69.ui.base.profile.PUBLIC
import com.i69.utils.Resource
import com.i69.utils.getCompressedImageFilePath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val appRepository: AppRepository,
    val userUpdateRepository: UserUpdateRepository,
    private val coinRepository: CoinRepository,
    private val userDetailsRepository: UserDetailsRepository
) : ViewModel() {

    val whitelistSignInClient: LiveData<List<GetAllSocialAuthStatusQuery.AllSocialAuthStatus?>?> get() = _whitelistSignInClient
    private val _whitelistSignInClient =
        MutableLiveData<List<GetAllSocialAuthStatusQuery.AllSocialAuthStatus?>?>()

    val nextScreenId: LiveData<Int> get() = _nextScreenId
    private val _nextScreenId = MutableLiveData<Int>()

    val updateLanguage: LiveData<Boolean> get() = _updateLanguage
    private val _updateLanguage = MutableLiveData<Boolean>()

    val contactErrorDialog: LiveData<Boolean> get() = _contactErrorDialog
    private val _contactErrorDialog = MutableLiveData<Boolean>()

    val errorMessage: LiveData<String> get() = _errorMessage
    private val _errorMessage = MutableLiveData<String>()

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager

    private var authUser: User? = null
    var token: String? = null

    var TAG: String = AuthViewModel::class.java.simpleName

    fun initializeSocialLogins(activity: Activity, userPreferences: UserPreferences?) {
        initializeGoogleSignIn(activity)
        initializeFacebookSignIn(activity, userPreferences)
    }

    private fun initializeGoogleSignIn(activity: Activity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("223314224724-skuq309vrsk69a7m0cmhkl1ska4qtd82.apps.googleusercontent.com")
            .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    private fun initializeFacebookSignIn(activity: Activity, userPreferences: UserPreferences?) {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
        loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY)
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.e(TAG, "Facebook: onSuccess: $result")
                getUserDataFromFacebook(result) { name, photos ->
                    GlobalScope.launch(Dispatchers.Main) {
                        onLoginSuccess(
                            activity,
                            LoginProvider.FACEBOOK,
                            fullName = name,
                            photo = photos?.firstOrNull() ?: "",
                            accessToken = result.accessToken.token,
                            userPreferences = userPreferences
                        )
                    }
                }
            }

            override fun onCancel() {
                Log.e(TAG, "Facebook: onCancel")
                _errorMessage.postValue("")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Facebook: onError: $error")
                _errorMessage.postValue(
                    "${activity.getString(R.string.sign_in_failed)} ${
                        activity.getString(
                            R.string.try_again_later
                        )
                    }"
                )
            }
        })
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return mGoogleSignInClient
    }

    fun getFacebookCallbackManager(): CallbackManager {
        return callbackManager
    }

    fun getFacebookLoginManager(): LoginManager {
        return loginManager
    }

    fun getDefaultPickers(userToken: String): LiveData<DefaultPicker> =
        appRepository.getDefaultPickers(viewModelScope, userToken)

    fun handleGoogleIntentResponse(
        context: Context, userPreferences: UserPreferences?, result: ActivityResult
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        Log.e(TAG, "Data : : ${result.data}")
        Log.e(TAG, "Code : : ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = task.getResult(ApiException::class.java)!!
                GlobalScope.launch {
                    onLoginSuccess(
                        context,
                        provider = LoginProvider.GOOGLE,
                        fullName = account.displayName,
                        photo = "",
                        accessToken = account.idToken!!,
                        userPreferences = userPreferences
                    )
                }
            } catch (e: ApiException) {
                if (e.statusCode != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    _errorMessage.postValue("${e.message}")
                }
            }
        } else {
            _errorMessage.postValue("")
        }
    }

    suspend fun availableSocialSignInClients(context: Context) {
        val response = loginRepository.getAvailableSocialSignInClients(context)
        if (response != null) {
            if (response.hasErrors()) {
                Log.e(TAG, "SocialLogin: HasError")
                val error = response.errors?.get(0)?.message
                _errorMessage.postValue(error.toString())
            } else {
                Log.e(TAG, "SocialLogin: SUCCESS")
                Log.e(TAG, "SocialLogin: Response: ${response.data}")
                _whitelistSignInClient.postValue(response.data?.allSocialAuthStatus)
            }
        } else {
            Log.e(TAG, "SocialLogin: Api error for social login clients")
            _errorMessage.postValue("")
        }
    }

    suspend fun login(loginRequest: LoginRequest): Resource<ResponseBody<LoginResponse>> =
        loginRepository.login(loginRequest)

    fun getUserDataFromFacebook(
        loginResult: LoginResult, callback: (String?, ArrayList<String>?) -> Unit
    ) = loginRepository.getUserDataFromFacebook(loginResult, callback)

    /// Coin
    suspend fun deductCoin(
        userId: String, token: String, deductCoinMethod: com.i69.data.enums.DeductCoinMethod
    ): Resource<ResponseBody<CoinsResponse>> =
        coinRepository.deductCoin(userId, token = token, deductCoinMethod)

    fun getAuthUser() = authUser

    fun setAuthUser(updated: User) {
        authUser = updated
    }

    suspend fun getLanguage(userId: String, token: String) =
        userDetailsRepository.getLanguage(userId, token).data

    /// Update
    suspend fun updateProfile(user: User, token: String): Resource<ResponseBody<Id>> =
        userUpdateRepository.updateProfile(user, token = token)

    suspend fun uploadImage(
        userId: String, token: String, filePath: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.uploadImage(userId = userId, token = token, filePath, PUBLIC)

    suspend fun uploadImage2(
        userId: String, token: String, filePath: File
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.uploadImage2(userId = userId, token = token, filePath, PUBLIC)

    suspend fun updateLanguage(
        languageCode: String, userid: String, token: String
    ): Resource<ResponseBody<Id>> = userUpdateRepository.updateLanguage(
        languageCode = languageCode, userid = userid, token = token
    )

    private suspend fun onLoginSuccess(
        context: Context,
        provider: LoginProvider,
        fullName: String? = "",
        photo: String,
        accessToken: String,
        accessVerifier: String = "",
        userPreferences: UserPreferences? = null
    ) {
        val name = fullName
        val loginRequest = LoginRequest(
            accessToken = accessToken, accessVerifier = accessVerifier, provider = provider.provider
        )

        when (val response = login(loginRequest)) {
            is Resource.Success -> {
                Log.e(TAG, "onLoginSuccess: ${response.data?.data}")
                val nameValue =
                    if (provider == LoginProvider.TWITTER) response.data?.data?.username else name
                val compressedFilePath =
                    if (photo.isNotEmpty()) context.getCompressedImageFilePath(photo) else ""
                val photos = if (compressedFilePath.isNullOrEmpty()) {
                    mutableListOf()
                } else {
                    val photoObject = Photo(id = "0", compressedFilePath, PUBLIC)
                    mutableListOf(photoObject)
                }
                FCMHandler.enableFCM()
                navigateToNextScreen(context, response, nameValue, photos, userPreferences)
            }

            is Resource.Error -> {
                if (response.message.toString().contains("contact us", true)) {
                    _contactErrorDialog.postValue(true)
                } else {
                    _errorMessage.postValue("${response.message.toString()}")
                }
            }

            else -> {

            }
        }
    }

    private suspend fun navigateToNextScreen(
        context: Context,
        response: Resource.Success<ResponseBody<LoginResponse>>,
        name: String?,
        photos: MutableList<Photo>,
        userPreferences: UserPreferences?
    ) {
        var userName = name
        if (userName == null) {
            userName = ""
        }
        val loginResult = response.data!!.data
        Log.e(TAG, "LoginResultIsNew: ${loginResult?.isNew}")
        Log.e(TAG, "Token: ${loginResult?.token}")
//        userPreferences?.saveUserIdToken(
//            userId = loginResult!!.id,
//            token = loginResult.token,
//            loginResult.username, loginResult.email!!
//        )
        if (loginResult?.isNew == true) {
            val email = loginResult.email?.substring(0, loginResult.email.indexOf("@")) ?: ""
            val names = userName.replace("_twitter_twitter", "").replace("_twitter", "")
            setAuthUser(
                User(
                    id = loginResult.id,
                    email = loginResult.email ?: "",
//                    fullName = names ?: email,
                    fullName = names,
                    avatarPhotos = photos,
                )
            )
            token = loginResult.token

            _nextScreenId.postValue(R.id.action_login_to_interested_in)
        } else {
            Log.e(TAG, "UserId: ${loginResult!!.id}")
            val names = userName.replace("_twitter_twitter", "").replace("_twitter", "")
            userPreferences?.saveUserIdToken(
                userId = loginResult.id,
                token = loginResult.token,
                names, loginResult.email!!
            )
            App.updateFirebaseToken(userUpdateRepository)
            updateLanguage(context, loginResult.id, loginResult.token)
        }
    }

    private fun updateLanguage(context: Context, id: String?, token: String?) {
        GlobalScope.launch {
            val languageModel = getLanguage(id.toString(), token.toString())
            val userLang = languageModel?.userLanguageCode
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val prefLanguage = pref.getString("language", "")
            val deviceLocale = userLang
                ?: if (prefLanguage.isNullOrEmpty()) Locale.getDefault().language else prefLanguage

            when (val response = updateLanguage(
                languageCode = deviceLocale, userid = id.toString(), token = token.toString()
            )) {
                is Resource.Success -> {
                    Log.e(TAG, "Response is success!")
                    pref?.edit()?.putString("language", deviceLocale)?.apply()
                    _updateLanguage.postValue(true)
                }

                is Resource.Error -> {
                    Log.e(TAG, "Response Error!")
                    _errorMessage.postValue("${"LanguageUpdate Failed"} ${response.message}")
                }

                else -> {
                    Log.e(TAG, "Else part!")
                }
            }
        }
    }
}