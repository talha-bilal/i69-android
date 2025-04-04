package com.i69.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.i69.R
import com.i69.data.config.Constants
import com.i69.data.preferences.UserPreferences
import com.i69.firebasenotification.NotificationBroadcast
import com.i69.singleton.App
import com.i69.utils.createLoadingDialog
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import kotlinx.coroutines.flow.first
import java.io.File


abstract class BaseFragment<dataBinding : ViewDataBinding> : Fragment() {


    protected var userPreferences: UserPreferences? = null
    protected var binding: dataBinding? = null
    var loadingDialog: Dialog? = null
    var navController: NavController? = null
    private var broadcast: NotificationBroadcast? = null
    private var TAG: String = BaseFragment::class.java.simpleName
    var hasInitializedRootView = false
    private var rootView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userPreferences = App.userPreferences
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setStatusBarColor(getStatusBarColor())
        binding = getFragmentBinding(inflater, container)
        val contentView = binding?.root
        broadcast = NotificationBroadcast(this)
        contentView?.findViewById<View>(R.id.actionBack)?.setOnClickListener {
            findNavController().popBackStack()
        }

        loadingDialog = requireActivity().createLoadingDialog()
        binding?.apply {
            lifecycleOwner = this@BaseFragment
        }

        setupTheme()
        initObservers()
        setupClickListeners()

        return binding?.root
    }

    fun getPublicDirectory(): File? {
        var directory: File? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/${resources.getString(R.string.app_name)}"
            )
        } else {
            File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/${resources.getString(R.string.app_name)}"
            )
        }

        if (!directory?.exists()!!) {
            // Make it, if it doesn't exit
            val success: Boolean = directory.mkdirs()
            if (!success) {
                directory = null
            }
        }

        return directory
    }

    fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): dataBinding
    abstract fun initObservers()
    abstract fun setupTheme()
    abstract fun setupClickListeners()
    suspend fun getCurrentUserId() = userPreferences?.userId?.first()
    suspend fun getCurrentUserName() = userPreferences?.userName?.first()
    suspend fun getCurrentUserToken() = userPreferences?.userToken?.first()
    suspend fun getChatUserId() = userPreferences?.chatUserId?.first()
    suspend fun getEmailId() = userPreferences?.userEmail?.first()
    open fun getStatusBarColor() = R.color.colorPrimaryDark
    fun setStatusBarColor(@ColorRes color: Int) {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), color)
    }

    protected fun showProgressWithoutBlocking(view: View) {
        view.findViewById<LinearLayoutCompat>(R.id.llProgressRoot)?.setViewVisible()
    }

    protected fun hideProgress(view: View) {
        view.findViewById<LinearLayoutCompat>(R.id.llProgressRoot)?.setViewGone()
    }

    protected fun showProgressView() {
        if (loadingDialog != null) {
            if (loadingDialog?.isShowing == false)
                loadingDialog?.show()
        }
    }

    protected fun hideProgressView() {
        if (loadingDialog != null) {
            if (loadingDialog!!.isShowing)
                loadingDialog?.dismiss()
        }
    }

    protected fun <T : Activity> getTypeActivity(): T? {
        return if (activity != null) activity as T else null
    }

    fun moveTo(direction: Int, args: Bundle? = null) =
        view?.findNavController()?.navigate(direction, args)

    fun moveTo(direction: NavDirections) = view?.findNavController()?.navigate(direction)

    open fun moveUp() = view?.findNavController()?.navigateUp()

    override fun onPause() {
        super.onPause()
        if (broadcast != null) {
            LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcast!!)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume...........................")
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcast!!, IntentFilter(Constants.INTENTACTION))
    }
}