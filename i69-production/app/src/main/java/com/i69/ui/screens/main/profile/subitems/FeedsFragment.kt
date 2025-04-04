package com.i69.ui.screens.main.profile.subitems

import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
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
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.databinding.FragmentFeedBinding
import com.i69.di.modules.AppModule
import com.i69.R
import com.i69.ui.adapters.NearbySharedMomentAdapter
import com.i69.ui.base.BaseFragment
import com.i69.utils.apolloClient
import com.i69.utils.getResponse
import com.i69.utils.snackbar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FeedsFragment : BaseFragment<FragmentFeedBinding>(),
    NearbySharedMomentAdapter.NearbySharedMomentListener {

    private var userToken: String? = null
    private var userName: String? = null
    private var TAG: String = FeedsFragment::class.java.simpleName
    private lateinit var sharedMomentAdapter: NearbySharedMomentAdapter

    var width = 0
    var size = 0
    var endCursor: String = ""
    var hasNextPage: Boolean = false
    var allUserMoments: ArrayList<GetAllUserMomentsQuery.Edge> = ArrayList()
    var layoutManager: LinearLayoutManager? = null

    private var userId: String? = null
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

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
        if (exoPlayer.isPlaying) exoPlayer.pause()
        if (this::sharedMomentAdapter.isInitialized) {
            sharedMomentAdapter.pauseAll()
        }
    }

    override fun onPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause()
        if (this::sharedMomentAdapter.isInitialized) {
            sharedMomentAdapter.pauseAll()
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

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFeedBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        navController = findNavController()
        viewStringConstModel.data.observe(this@FeedsFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
//            Log.e("MydataBasesss", it.value!!.messages)
        }
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels

        val densityMultiplier = getResources().getDisplayMetrics().density;
        val scaledPx = 14 * densityMultiplier;
        val paint = Paint()
        paint.setTextSize(scaledPx);
        size = paint.measureText("s").roundToInt()

        setUpData()
    }

    override fun setupClickListeners() {

    }

    private fun setUpData() {

        lifecycleScope.launch {
            if (getCurrentUserId() != null) {
                userId = getCurrentUserId()!!
            }
            getCurrentUserToken()?.let {
                userToken = it

                getAllUserMoments(width, size)
            }
            userName = getCurrentUserName()
        }

        allUserMoments = ArrayList()
        sharedMomentAdapter = NearbySharedMomentAdapter(
            requireActivity(),
            this,
            allUserMoments,
            userId,
            false
        )
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding?.rvMoments?.setLayoutManager(layoutManager)


        binding?.scrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (hasNextPage) {

                binding?.rvMoments?.let {
                    if (it.bottom - ((binding?.scrollView?.height
                            ?: 0) + (binding?.scrollView?.scrollY ?: 0)) == 0
                    )
                        allusermoments1(width, size, 10, endCursor)
                }
            }
        })
    }


    private fun getAllUserMoments(width: Int, size: Int) {
        lifecycleScope.launch() {
            userToken = getCurrentUserToken()!!
            val res = try {
                Log.e("getAllUserMoments", "${userToken}")
                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserMomentsQuery(
                        width, size, 10, "", ""
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e("getAllUserMoments", "${e.message}")
                Log.e(TAG, "apolloResponse ${e.message}")

                return@launch
            }

            val allmoments = res.data?.allUserMoments!!.edges
            Log.e("getAllUserMoments", "${allmoments.size}")
            if (allmoments.size != 0) {
                endCursor = res.data?.allUserMoments!!.pageInfo.endCursor!!
                hasNextPage = res.data?.allUserMoments!!.pageInfo.hasNextPage!!

                val allUserMomentsFirst: ArrayList<GetAllUserMomentsQuery.Edge> = ArrayList()

                allmoments.indices.forEach { i ->
                    allUserMomentsFirst.add(allmoments[i]!!)
                }

                binding?.rvMoments?.adapter = sharedMomentAdapter
                allUserMoments.addAll(allUserMomentsFirst)
                sharedMomentAdapter.submitList1(allUserMoments)
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

    private fun getParticularMoments(pos: Int, ids: String) {
        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(
                        GetAllUserMomentsQuery(
                            width,
                            size,
                            1,
                            "",
                            ids
                        )
                    )
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all moments${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }

                val allmoments = res.data?.allUserMoments!!.edges

                allmoments.indices.forEach { i ->
                    if (ids.equals(allmoments[i]!!.node!!.pk.toString())) {
                        if (allUserMoments.size > 0) {
                            allUserMoments[pos] = allmoments[i]!!
                            sharedMomentAdapter.submitList1(allUserMoments)
                            sharedMomentAdapter.notifyItemChanged(pos)
                        }
                        return@forEach
                    }
                }
            }
        }
    }

    private fun fireLikeNotificationforreceiver(item: GetAllUserMomentsQuery.Edge) {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val queryName = "sendNotification"
                val query = StringBuilder()
                    .append("mutation {")
                    .append("$queryName (")
                    .append("userId: \"${item!!.node!!.user!!.id}\", ")
                    .append("notificationSetting: \"LIKE\", ")
                    .append("data: {momentId:${item!!.node!!.pk}}")
                    .append(") {")
                    .append("sent")
                    .append("}")
                    .append("}")
                    .toString()

                val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                    query,
                    queryName, userToken
                )
                Log.e("RSLT", "" + result.message)
            }
        }
    }

    private fun allusermoments1(width: Int, size: Int, i: Int, endCursors: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(
                        GetAllUserMomentsQuery(
                            width,
                            size,
                            10,
                            endCursors,
                            ""
                        )
                    )
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all moments ${e.message}")

                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }

                val allusermoments = res.data?.allUserMoments!!.edges

                endCursor = res.data?.allUserMoments!!.pageInfo.endCursor!!
                hasNextPage = res.data?.allUserMoments!!.pageInfo.hasNextPage

                if (allusermoments.size != 0) {
                    val allUserMomentsNext: ArrayList<GetAllUserMomentsQuery.Edge> = ArrayList()

                    allusermoments.indices.forEach { i ->
                        allUserMomentsNext.add(allusermoments[i]!!)
                    }
                    allUserMoments.addAll(allUserMomentsNext)
                    sharedMomentAdapter.submitList1(allUserMoments)

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
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node!!.file}")
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node!!.id}")
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node!!.createdDate}")
                    Log.e(
                        TAG,
                        "apolloResponse: ${allusermoments.get(0)?.node!!.momentDescriptionPaginated}"
                    )
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.node!!.user?.fullName}")
                }
            }
        }
    }

    override fun onCommentofMomentClick(
        position: Int,
        item: GetAllUserMomentsQuery.Edge?
    ) {
        val bundle = Bundle()
        bundle.putString("momentID", item?.node!!.pk!!.toString())

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

    override fun onMomentGiftClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {
    }


    override fun onDotMenuofMomentClick(
        position: Int,
        item: GetAllUserMomentsQuery.Edge?,
        types: String
    ) {

        if (types.equals("delete")) {
            showProgressView()
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    userToken = getCurrentUserToken()!!
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(DeletemomentMutation(item?.node!!.pk!!.toString()))
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponseException ${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }

                    hideProgressView()

                    val positionss = allUserMoments.indexOf(item)
                    allUserMoments.remove(item)
                    sharedMomentAdapter.notifyItemRemoved(position)
                }
            }
        } else if (types.equals("report")) {
            showProgressView()
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    userToken = getCurrentUserToken()!!
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(
                            ReportonmomentMutation(
                                item?.node!!.pk!!.toString(),
                                "This is not valid post"
                            )
                        )
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception ${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }

                    hideProgressView()
                }
            }
        }
    }


    override fun onMoreShareMomentClick() {

    }

    override fun onSharedMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {
    }


    override fun onLikeofMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {

        showProgressView()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                userId = getCurrentUserId()!!
                userName = getCurrentUserName()!!
                Log.i("FeedsFragment", "onLikeofMomentClick: UserId: $userId   Username: $userName")
                val res = try {
                    apolloClient(
                        requireContext(),
                        userToken!!
                    ).mutation(LikeOnMomentMutation(item?.node!!.pk!!.toString()))
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

    override fun onLikeofMomentshowClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {

    }
}