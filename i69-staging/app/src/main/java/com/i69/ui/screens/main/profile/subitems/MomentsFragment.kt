package com.i69.ui.screens.main.profile.subitems

import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.*
import com.i69.data.models.User
import com.i69.databinding.FragmentFeedBinding
import com.i69.di.modules.AppModule
import com.i69.R
import com.i69.ui.adapters.CurrentUserMomentAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.utils.AnimationTypes
import com.i69.utils.EXTRA_USER_MODEL
import com.i69.utils.apolloClient
import com.i69.utils.getResponse
import com.i69.utils.navigate
import com.i69.utils.snackbar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MomentsFragment() : BaseFragment<FragmentFeedBinding>(),
    CurrentUserMomentAdapter.CurrentUserMomentListener {

    private var userToken: String? = null
    private lateinit var currentUserMomentAdapter: CurrentUserMomentAdapter
    private var TAG: String = MomentsFragment::class.java.simpleName
    var user: User? = null

    var width = 0
    var size = 0
//    private var id_s: String? = null

    private var userId: String? = null
    private var userName: String? = null

    var endCursor: String = ""
    var hasNextPage: Boolean = false
    var allUserMoments: ArrayList<GetUserMomentsQuery.Edge> = ArrayList()
    var layoutManager: LinearLayoutManager? = null

    private lateinit var onAllMomentsDeleted: (Boolean) -> Unit

    private lateinit var exoPlayer: ExoPlayer

    override fun playVideo(mediaItem: MediaItem, playWhenReady: Boolean): ExoPlayer {
        exoPlayer.apply {
            setMediaItem(mediaItem, false)
            this.playWhenReady = playWhenReady
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
        return exoPlayer
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun pauseVideo() {
        if (isPlaying())
            exoPlayer.pause()
    }

    fun pauseVideoOnSwipe() {
        if (this::exoPlayer.isInitialized) {
            if (exoPlayer.isPlaying) exoPlayer.pause()
        }
        if (this::currentUserMomentAdapter.isInitialized) {
            currentUserMomentAdapter.pauseAll()
        }
    }

    override fun onPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause()
        if (this::currentUserMomentAdapter.isInitialized) {
            currentUserMomentAdapter.pauseAll()
        }
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
        exoPlayer.release()
    }

    fun setOnAllMomentsDeleted(onAllMomentsDeleted: (Boolean) -> Unit) {
        this.onAllMomentsDeleted = onAllMomentsDeleted
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFeedBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        navController = findNavController()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels

        val densityMultiplier = resources.displayMetrics.density
        val scaledPx = 14 * densityMultiplier
        val paint = Paint()
        paint.setTextSize(scaledPx)
        size = paint.measureText("s").roundToInt()

        setUpData()
    }

    override fun setupClickListeners() {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val arguments = arguments
//        if (arguments != null) {
//            id_s = arguments.get("ID") as String?
//        }

        if (arguments?.containsKey(EXTRA_USER_MODEL) == true) {
            try {
                user = (Gson().fromJson(arguments?.getString(EXTRA_USER_MODEL), User::class.java)
                    ?: "") as User?
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    private fun setUpData() {
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
            userName = getCurrentUserName()

            getAllUserMoments(width, size)
        }

        allUserMoments = ArrayList()
        currentUserMomentAdapter = CurrentUserMomentAdapter(
            requireActivity(),
            this,
            allUserMoments,
            userId
        )
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding?.rvMoments?.setLayoutManager(layoutManager)

//        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (hasNextPage) {
//                binding.rvMoments?.let {
//                    if (it.bottom - (binding.scrollView.height + binding.scrollView.scrollY) == 0)
//                        allusermoments1(width,size,10,endCursor)
//                }
//            }
//        })
    }

    private fun getAllUserMoments(width: Int, size: Int) {

        Log.e(TAG, "getAllUserMoments: $width $size")

        if (user?.id == "") {
            user!!.id = userId!!
        }
        lifecycleScope.launch() {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetUserMomentsQuery(width, size, 10, "", user?.id.toString(), "")
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloException currentUserMoments ${e.message}")
                return@launch
            }

            val allmoments = res.data?.allUserMoments?.edges
            if (!allmoments.isNullOrEmpty()) {
                endCursor = res.data?.allUserMoments?.pageInfo?.endCursor!!
                hasNextPage = res.data?.allUserMoments?.pageInfo?.hasNextPage!!


                val allUserMomentsFirst: ArrayList<GetUserMomentsQuery.Edge> = ArrayList()

                for (item in allmoments) {
                    if (item != null) {
                        allUserMomentsFirst.add(item)
                    }
                }

                currentUserMomentAdapter.addAll(allUserMomentsFirst)
                binding?.rvMoments?.adapter = currentUserMomentAdapter
            }

            if (binding?.rvMoments?.itemDecorationCount == 0) {
                binding?.rvMoments?.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.top = 10
                    }
                })
            }
        }
    }


    override fun onLikeofMomentClick(position: Int, item: GetUserMomentsQuery.Edge) {
        showProgressView()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(
                        requireContext(),
                        userToken!!
                    ).mutation(LikeOnMomentMutation(item.node!!.pk!!.toString()))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
                }

                hideProgressView()

                fireLikeNotificationforreceiver(item)

                getParticularMoments(position, item.node.pk.toString())
            }
        }
    }


    private fun getParticularMoments(pos: Int, ids: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(
                        GetUserMomentsQuery(
                            width,
                            size,
                            1,
                            "",
                            user!!.id,
                            ids
                        )
                    )
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all moments ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }

                val allmoments = res.data?.allUserMoments!!.edges

                allmoments.indices.forEach { i ->
                    if (ids.equals(allmoments[i]!!.node!!.pk.toString())) {
                        allUserMoments[pos] = allmoments[i]!!
                        currentUserMomentAdapter.addAll(allUserMoments)
                        currentUserMomentAdapter.notifyItemChanged(pos)
                        return@forEach
                    }
                }
            }
        }
    }


    private fun fireLikeNotificationforreceiver(item: GetUserMomentsQuery.Edge?) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                val queryName = "sendNotification"
                val query = StringBuilder()
                    .append("mutation {")
                    .append("$queryName (")
                    .append("userId: \"${item!!.node!!.user!!.id}\", ")
                    .append("notificationSetting: \"LIKE\", ")
                    .append("data: {momentId:${item.node!!.pk}}")
                    .append(") {")
                    .append("sent")
                    .append("}")
                    .append("}")
                    .toString()

                val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                    query,
                    queryName, userToken
                )
                Log.e(TAG, "RSLT" + "" + result.message)
            }
        }
    }


    fun allusermoments1(width: Int, size: Int, i: Int, endCursors: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(
                        GetUserMomentsQuery(
                            width, size, i, endCursors,
                            userId!!, ""
                        )
                    )
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all moments ${e.message}")

                    binding?.root?.snackbar("${e.message}")
                    return@repeatOnLifecycle
                }

                val allusermoments = res.data?.allUserMoments?.edges

                if (!allusermoments.isNullOrEmpty()) {
                    endCursor = res.data?.allUserMoments?.pageInfo?.endCursor!!
                    hasNextPage = res.data?.allUserMoments?.pageInfo?.hasNextPage!!

                    val allUserMomentsNext: ArrayList<GetUserMomentsQuery.Edge> = ArrayList()

                    allusermoments.indices.forEach { i ->
                        allUserMomentsNext.add(allusermoments[i]!!)
                    }
                    currentUserMomentAdapter.addAll(allUserMomentsNext)
                }

                if (binding?.rvMoments?.itemDecorationCount == 0) {
                    binding?.rvMoments?.addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            outRect.top = 10
                        }
                    })
                }
                if (allusermoments?.size!! > 0) {
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node?.file}")
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node?.id}")
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node?.createdDate}")
                    Log.e(
                        TAG,
                        "apolloResponse: ${allusermoments.get(0)?.node?.momentDescriptionPaginated}"
                    )
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node?.user?.fullName}")
                }
            }
        }
    }

    override fun onCommentofMomentClick(
        position: Int,
        item: GetUserMomentsQuery.Edge
    ) {
        val bundle = Bundle()
        bundle.putString("momentID", item.node!!.pk!!.toString())

        bundle.putString("filesUrl", item.node.file!!)
        bundle.putString("Likes", item.node.like!!.toString())
        bundle.putString("Comments", item.node.comment!!.toString())
        val gson = Gson()
        bundle.putString("Desc", gson.toJson(item.node.momentDescriptionPaginated))
        if (item.node.user!!.gender != null) {
            bundle.putString("gender", item.node.user.gender!!.name)
        } else {
            bundle.putString("gender", null)
        }
        bundle.putString("fullnames", item.node.user.fullName)
        bundle.putString("momentuserID", item.node.user.id.toString())

        navController?.navigate(R.id.momentsAddCommentFragment, bundle)
    }

    override fun onDotMenuofMomentClick(
        position: Int,
        item: GetUserMomentsQuery.Edge, types: String
    ) {
        if (types.equals("delete")) {

            showProgressView()
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(DeletemomentMutation(item.node!!.pk!!.toString()))
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }

                    hideProgressView()

                    val positionss = allUserMoments.indexOf(item)
                    allUserMoments.remove(item)
                    currentUserMomentAdapter.notifyItemRemoved(position)
                    if (currentUserMomentAdapter.itemCount == 0)
                        onAllMomentsDeleted.invoke(true)
                }
            }
        } else if (types.equals("report")) {
            showProgressView()
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(
                            ReportonmomentMutation(
                                item.node!!.pk!!.toString(),
                                "This is not valid post"
                            )
                        )
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }

                    hideProgressView()
                }
            }
        }
    }

    override fun onProfileOpen(position: Int, edge: GetUserMomentsQuery.Edge) {
        val mUserID = edge.node?.user?.id.toString()
        Log.e(TAG, "onProfileOpen:mUserId: $mUserID")
        val bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", mUserID)
        Log.e(TAG, "onProfileOpen:userId: $userId")
        if (userId == mUserID) {
            MainActivity.getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                R.id.nav_user_profile_graph
        } else {
            findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
        }
    }

    override fun onMomentGiftClick(position: Int, item: GetUserMomentsQuery.Edge?) {
//        var bundle = Bundle()
//        bundle.putString("userId", userId)
//        navController.navigate(R.id.action_userProfileFragment_to_userGiftsFragment,bundle)
    }

    override fun onMoreShareMomentClick() {

    }

    override fun onSharedMomentClick(position: Int, item: GetUserMomentsQuery.Edge) {
    }
}