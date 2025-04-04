package com.i69.ui.base.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.exception.ApolloException
import com.i69.GetNotificationCountQuery
import com.i69.R
import com.i69.databinding.FragmentSearchInterestedInBinding
import com.i69.ui.adapters.SearchInterestedServerAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.apolloClient
import com.i69.utils.snackbar
import kotlinx.coroutines.launch

abstract class BaseSearchFragment : BaseFragment<FragmentSearchInterestedInBinding>(),
    SearchInterestedServerAdapter.SearchInterestedListener {

    private var userToken: String? = null
    private var userId: String? = null
    private var TAG: String = BaseSearchFragment::class.java.simpleName

    companion object {
        var showAnim = true
    }

    val mViewModel: UserViewModel by viewModels()
    protected val viewModel: UserViewModel by activityViewModels()
    protected lateinit var adapter: SearchInterestedServerAdapter

    abstract fun setScreenTitle()

    abstract fun setupChiledTheme()

    abstract fun initDrawerStatus()

    abstract fun getItems(): List<SearchInterestedServerAdapter.MenuItemString>

    abstract fun onAdapterItemClick(pos: Int)


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchInterestedInBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        setScreenTitle()
        navController = findNavController()
        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            userId = getCurrentUserId()!!
            Log.e(TAG, "usertokenn $userToken")
            getNotificationIndex()
        }
        adapter = SearchInterestedServerAdapter(0, getAnim(), this)
        showAnim = false
        binding?.searchChoiceItems?.adapter = adapter
        setupChiledTheme()
        initDrawerStatus()
    }

    private fun getNotificationIndex() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(GetNotificationCountQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception NotificationIndex${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                Log.e(TAG, "apolloResponse NotificationIndex ${res.hasErrors()}")

                val notifyCount = res.data?.unseenCount
                if (notifyCount == null || notifyCount == 0) {
                    binding?.counter?.visibility = View.GONE
                } else {
                    binding?.counter?.visibility = View.VISIBLE
                    if (notifyCount > 10) {
                        binding?.counter?.text = "9+"
                    } else {
                        binding?.counter?.text = "$notifyCount"
                    }
                }
            }
        }
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            getMainActivity().drawerSwitchState()
        }

        binding?.bell?.setOnClickListener {
            val dialog =
                NotificationDialogFragment(userToken, binding?.counter, userId, binding?.bell)
            getMainActivity().notificationDialog(
                dialog,
                childFragmentManager,
                "${requireActivity().resources.getString(R.string.notifications)}"
            )
        }
    }

    override fun onViewClick(pos: Int) {
        onAdapterItemClick(pos)
    }

    protected open fun getAnim(): Boolean = showAnim

    fun getMainActivity() = activity as MainActivity

}