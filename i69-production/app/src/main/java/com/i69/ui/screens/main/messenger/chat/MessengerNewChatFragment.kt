package com.i69.ui.screens.main.messenger.chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import com.devlomi.record_view.OnRecordListener
import com.devlomi.record_view.RecordPermissionHandler
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.i69.BuildConfig
import com.i69.DeleteUserMessagesMutation
import com.i69.GetBroadcastMessageListQuery
import com.i69.GetChatMessagesByRoomIdQuery
import com.i69.GetFirstMessageListQuery
import com.i69.GetLastSeenMessageMutation
import com.i69.GetLastSeenMessageUserQuery
import com.i69.GiftPurchaseMutation
import com.i69.IsOnlineQuery
import com.i69.LastSeenMessageByReceiverSubscription
import com.i69.OnDeleteMessageSubscription
import com.i69.OnNewMessageSubscription
import com.i69.OnUpdatePrivateRequestSubscription
import com.i69.PrivatePhotosDecisionMutation
import com.i69.R
import com.i69.SendChatMessageMutation
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.ModelGifts
import com.i69.data.models.User
import com.i69.data.remote.responses.ResponseBody
import com.i69.databinding.AlertFullImageBinding
import com.i69.databinding.FragmentNewMessengerChatBinding
import com.i69.di.modules.AppModule
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.type.MessageMessageType
import com.i69.ui.adapters.NewChatMessagesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.camera.CameraActivity
import com.i69.ui.screens.main.messenger.chat.chatList.ChatMessageListener
import com.i69.ui.screens.main.messenger.chat.contact.Contact
import com.i69.ui.screens.main.messenger.chat.contact.ContactActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.screens.main.video.VideoPlayerActivity
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.AnimationTypes
import com.i69.utils.Resource
import com.i69.utils.UploadUtility
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientSubscription
import com.i69.utils.convertPXtoDP
import com.i69.utils.convertURITOBitmapNSaveImage
import com.i69.utils.copyToClipboard
import com.i69.utils.getGraphqlApiBody
import com.i69.utils.getMimeType
import com.i69.utils.getResponse
import com.i69.utils.hasPermissions
import com.i69.utils.hideKeyboard
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.i69.utils.navigate
import com.i69.utils.reportUserAccount
import com.i69.utils.isLocationEnabled
import com.i69.utils.setViewGone
import com.i69.utils.setViewMargins
import com.i69.utils.snackbar
import com.paypal.pyplcheckout.ui.feature.sca.runOnUiThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.webrtc.Camera2Enumerator
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import java.io.File
import java.util.UUID

@AndroidEntryPoint
class MessengerNewChatFragment : BaseFragment<FragmentNewMessengerChatBinding>(),
    ChatMessageListener {

    private lateinit var chatAdapter: NewChatMessagesAdapter
    private var serverDataList: ArrayList<GetChatMessagesByRoomIdQuery.Edge?> = ArrayList()
    private lateinit var showBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    var endCursor: String = ""
    var hasNextPage: Boolean = false
    private var isUpdatesChatView: Boolean = false
    private lateinit var deferred: Deferred<Unit>
    private var userId: String? = null
    private var userToken: String? = null
    private var currentUser: User? = null
    private val viewModel: UserViewModel by activityViewModels()
    var giftUserid: String? = null
    private var otherFirstName: String? = null
    private var otherUserId: String? = null
    private var chatType: String? = null
    var fragVirtualGifts: FragmentVirtualGifts? = null
    var fragRealGifts: FragmentRealGifts? = null
    private var otherUserProfile: String = "default"
    private lateinit var giftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var isProgressShow: Boolean = true
    private var isMessageSending: Boolean = false
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var audioRecorder: AudioRecorder? = null
    private var recordFile: File? = null
    var roomId = -1
    var screenHeight = 0
    var TAG: String = MessengerNewChatFragment::class.java.simpleName

    //    private lateinit var peerConnectionFactory: PeerConnectionFactory
//    private var videoCapturer: VideoCapturer? = null
//    private var videoTrack: VideoTrack? = null
//    private var audioTrack: AudioTrack? = null
//    private var videoSource: VideoSource? = null
//    private var audioSource: AudioSource? = null
//    private var localPeerConnection: PeerConnection? = null
//    private var remotePeerConnection: PeerConnection? = null
//    private var eglBase: EglBase? = null
    private var localVideoView: SurfaceViewRenderer? = null
    private var remoteVideoView: SurfaceViewRenderer? = null
    private val PERMISSIONS_REQUEST_CODE = 1001
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var localPeer: PeerConnection? = null
    private var remotePeer: PeerConnection? = null
    private var localStream: MediaStream? = null
    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:eu-turn4.xirsys.com").createIceServer(),
        PeerConnection.IceServer.builder("turn:eu-turn4.xirsys.com:80?transport=udp")
            .setUsername("your-username")
            .setPassword("your-password")
            .createIceServer()
    )
    private val signalingClient = ApolloClient.Builder()
        .serverUrl("https://api.chatadmin-mod.click/")
        .webSocketServerUrl("wss://api.chatadmin-mod.click/ws/graphql")
        .okHttpClient(OkHttpClient.Builder().build())
        .build()

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentNewMessengerChatBinding =
        FragmentNewMessengerChatBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun setupTheme() {
        roomId = arguments?.getInt("chatId") ?: 0
        chatType = arguments?.getString("ChatType")

        if (roomId == 0) {
            binding?.llCoinLeft?.setViewGone()
        }

        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
            setupData(true)
            initObservers()
            initInputListener()
            subscribeonUpdatePrivatePhotoRequest()
            audioRecorder()
            initChatAdapter()
//            handleKeyboardVisibility(binding?.root!!)
//            detectKeyboardUsingInputMethodManager(binding?.root!!)
            if (!chatType.equals("001") && !chatType.equals("000")) {
                subscribeOnDeleteMessage()
                subscribeOnNewMessage()
                subscribeOnLastSeenMessage()
                getLastSeenMessageId(roomId)
            }
        }

        viewStringConstModel.data.observe(this@MessengerNewChatFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }

        handleKeyboardVisibility(binding?.root!!)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
    }

    private fun handleKeyboardVisibility(rootView: View) {
        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Handler(Looper.getMainLooper()).postDelayed({
                    val rect = Rect()
                    rootView.getWindowVisibleDisplayFrame(rect)
                    val keypadHeight = screenHeight - rect.bottom

                    if (keypadHeight > screenHeight * 0.15) {
                        scrollToBottom()
                    }
                }, 0)
            }
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }

    private fun initChatAdapter() {
        chatAdapter = NewChatMessagesAdapter(
            requireActivity(), userId, viewLifecycleOwner, this@MessengerNewChatFragment
        )
        chatAdapter.otherUserAvtar = otherUserProfile

        (binding?.rvChatMessages?.layoutManager as LinearLayoutManager).apply {
            reverseLayout = true
            stackFromEnd = true

            binding?.rvChatMessages?.layoutManager = this
        }
        binding?.rvChatMessages?.setHasFixedSize(true)
        binding?.rvChatMessages?.adapter = chatAdapter
        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    scrollToBottom()
                }
            }
        })

        binding?.rvChatMessages?.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == chatAdapter.itemCount - 1) {
                        if (hasNextPage) {
                            showProgressView()
                            getChatMessages()
                        }
                    }
                }
            })
    }

    private fun audioRecorder() {
        audioRecorder = AudioRecorder()
        binding?.recordButton?.setRecordView(binding?.recordView!!)
        binding?.recordView?.setLockEnabled(false);
        binding?.recordView?.setRecordPermissionHandler(RecordPermissionHandler {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return@RecordPermissionHandler true
            }
            val recordPermissionAvailable = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
            if (recordPermissionAvailable) {
                return@RecordPermissionHandler true
            }
            permissionAudioRecorder.launch(microPhonePermission)
            false
        })

        binding?.recordView?.cancelBounds = 8f
        binding?.recordView?.setSmallMicColor(Color.parseColor("#c2185b"))
        binding?.recordView?.setLessThanSecondAllowed(false)
        binding?.recordView?.setSlideToCancelText("Slide To Cancel")
        binding?.recordView?.timeLimit = 600000//10 minutes

        binding?.recordView?.setOnRecordListener(object : OnRecordListener {
            override fun onStart() {
                recordFile =
                    File(requireActivity().filesDir, UUID.randomUUID().toString() + ".3gp")
                audioRecorder?.start(recordFile!!.path)
                binding?.recordView?.visibility = View.VISIBLE
            }

            override fun onCancel() {
                stopRecording(true)
                binding?.recordView?.setOnBasketAnimationEndListener {
                    binding?.recordView?.visibility = View.GONE
                }
            }

            override fun onFinish(recordTime: Long, limitReached: Boolean) {
                stopRecording(false)
                binding?.recordView?.visibility = View.GONE
                UploadUtility(this@MessengerNewChatFragment).uploadFile(
                    recordFile!!.path, authorization = userToken, upload_type = "audio"
                ) { url ->
                    if (url.equals("url")) {
                        binding?.root?.snackbar(AppStringConstant1.no_enough_coins,
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(
                                    destinationId = R.id.actionGoToPurchaseFragment,
                                    popUpFragId = null,
                                    animType = AnimationTypes.SLIDE_ANIM,
                                    inclusive = true,
                                )
                            })
                    } else {
                        var input = url
                        if (url?.startsWith("/media/chat_files/") == true) {
                            input = "${BuildConfig.BASE_URL}$url"
                        }
                        sendMessageToServer(input)
                    }
                }
            }

            override fun onLessThanSecond() {
                stopRecording(true)
                binding?.recordView?.visibility = View.GONE
            }

            override fun onLock() {}
        })
    }

    private fun stopRecording(deleteFile: Boolean) {
        audioRecorder!!.stop()
        if (recordFile != null && deleteFile) {
            recordFile!!.delete()
        }
    }

    override fun initObservers() {
        viewModel.currentUserLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                currentUser = it
                val coinsTextResource = if (currentUser!!.purchaseCoins == 0) {
                    binding?.coinsCounter?.text = currentUser!!.giftCoins.toString()
                    if (currentUser!!.giftCoins == 0 || currentUser!!.giftCoins == 1) {
                        AppStringConstant1.coins
                    } else {
                        AppStringConstant1.coins
                    }
                } else {
                    binding?.coinsCounter?.text = currentUser!!.purchaseCoins.toString()
                    if (currentUser!!.purchaseCoins == 0 || currentUser!!.purchaseCoins == 1) {
                        AppStringConstant1.coins
                    } else {
                        AppStringConstant1.coins
                    }
                }
                binding?.coinsLeftTv?.text = coinsTextResource
            }
        }
    }

    override fun setupClickListeners() {
        showBottomSheetBehavior = BottomSheetBehavior.from(binding?.showImageVideoBottomSheet!!)
        binding?.imgClose1?.setOnClickListener {
            showBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        showBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    private fun getCurrentUserDetails() {
        viewModel.getCurrentUserUpdate(userId!!, token = userToken!!, true)
    }

    private fun setupData(isProgressShow: Boolean) {
        this.isProgressShow = isProgressShow
        if (chatType.equals("001")) {
            getFirstMessages()
            otherUserProfile = ""
            binding?.inputLayout?.visibility = View.GONE
            binding?.userName?.text = requireArguments().getString("otherUserName")
            otherFirstName = requireArguments().getString("otherUserName")
            binding?.userProfileImg?.loadCircleImage(R.drawable.ic_chat_item_logo_new)
            binding?.actionReportMes?.visibility = View.GONE
        } else if (chatType.equals("000")) {
            getBrodcastMessages()
            otherUserProfile = ""
            binding?.inputLayout?.visibility = View.GONE
            binding?.userName?.text = requireArguments().getString("otherUserName")
            otherFirstName = requireArguments().getString("otherUserName")
            binding?.actionReportMes?.visibility = View.GONE
            binding?.userProfileImg?.loadCircleImage(R.drawable.ic_chat_item_logo_new)
        } else {
            endCursor = ""
            updateCoinView()
            initInputListener()
            binding?.inputLayout?.visibility = View.VISIBLE
            giftUserid = arguments?.getString("otherUserId")
            otherUserId = arguments?.getString("otherUserId")
            otherFirstName = requireArguments().getString("otherUserName")
            binding?.userName?.text = requireArguments().getString("otherUserName")
            binding?.sendgiftto?.text =
                AppStringConstant1.send_git_to + " " + requireArguments().getString("otherUserName")
            val url = requireArguments().getString("otherUserPhoto")
            binding?.userProfileImg?.loadCircleImage(url.toString())
            if (!url.isNullOrEmpty()) {
                otherUserProfile = url.toString()
            }
            isOtherUserOnline()
            binding?.userProfileImg?.setOnClickListener {
                gotoChatUserProfile()
            }
            binding?.userName?.setOnClickListener {
                gotoChatUserProfile()
            }
            val gender = requireArguments().getInt("otherUserGender", 0)
            binding?.input?.updateGiftIcon(gender)
            updateGiftIcon(gender)
        }
        binding?.closeBtn?.setOnClickListener {
            moveUp()
        }
        binding?.actionReportMes?.setOnClickListener {
            openMenuItem()
        }

        binding?.llCoinLeft?.setOnClickListener {
            findNavController().navigate(
                destinationId = R.id.actionGoToPurchaseFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
            )
        }

        binding?.imgAudioCall?.setOnClickListener {
            requestPermissionss()
//                lifecycleScope.launch {
//                    when (val response = viewModel.audioChat(
//                        1450,
//                        "audio chat",
//                        "6fc3e042-8dfe-4ebe-b762-dc51ecbae6a3",
//                        "A",
//                        "{\"key\": \"value\"}",
//                        userToken!!
//                    )) {
//                        is Resource.Success -> {
//
//                        }
//
//                        is Resource.Error -> {
//
//                        }
//
//                        else -> {}
//                    }
//                }
        }

        binding?.imgVideoCall?.setOnClickListener {
            lifecycleScope.launch {
                when (val response = viewModel.audioChat(
                    roomId, "Video chat",
                    "", "V", "{\"key\": \"value\"}", userToken!!
                )) {
                    is Resource.Success -> {

                    }

                    is Resource.Error -> {

                    }

                    else -> {}
                }
            }
        }
    }

    private fun requestPermissionss() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest)
        } else {
            openFullScreenCallDialog()
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filterValues { !it }.keys

            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(requireActivity(), "Permissions denied", Toast.LENGTH_SHORT)
                    .show()
            } else {
                openFullScreenCallDialog()
            }
        }

    private fun openFullScreenCallDialog() {
        val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.video_call_screen, null)
        dialog.setContentView(view)

        val imgAudioCall = view.findViewById<ImageView>(R.id.imgAudioCall)
        val imgVideoCall = view.findViewById<ImageView>(R.id.imgVideoCall)
        val imgEndCall = view.findViewById<ImageView>(R.id.imgEndCall)
        remoteVideoView = view.findViewById<SurfaceViewRenderer>(R.id.remoteVideoView)
        localVideoView = view.findViewById<SurfaceViewRenderer>(R.id.localVideoView)

//        initWebRTC()
        initializePeerConnectionFactory()

        imgAudioCall.setOnClickListener {

        }
        imgVideoCall.setOnClickListener {

        }
        imgEndCall.setOnClickListener {
            endCall()
            if (dialog.isShowing)
                dialog.dismiss()
        }

        val topMargin = 0 // Top margin in pixels
        val bottomMargin = 0 // Bottom margin in pixels
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

    private fun initializePeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(requireContext())
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    EglBase.create().eglBaseContext,
                    true,
                    true
                )
            )
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(EglBase.create().eglBaseContext))
            .createPeerConnectionFactory()

        setupLocalStream()
    }

    private fun setupLocalStream() {
        val videoSource = peerConnectionFactory?.createVideoSource(false)
        val videoCapturer = createVideoCapturer()
        videoCapturer?.initialize(
            SurfaceTextureHelper.create("CaptureThread", EglBase.create().eglBaseContext),
            requireActivity(),
            videoSource?.capturerObserver
        )
        videoCapturer?.startCapture(1280, 720, 30)

        val localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
        localStream = peerConnectionFactory?.createLocalMediaStream("mediaStream")
        localStream?.addTrack(localVideoTrack)

        localVideoView?.init(EglBase.create().eglBaseContext, null)
        localVideoView?.setMirror(true)
        localVideoTrack?.addSink(localVideoView)

        setupSignaling()
    }

    private fun createVideoCapturer(): VideoCapturer? {
        return Camera2Enumerator(requireActivity()).run {
            deviceNames.find { isFrontFacing(it) }?.let { createCapturer(it, null) }
        }
    }

    private fun setupSignaling() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                signalingClient.subscription(YourSubscription()).execute {
//                    when (it.data?.newCall?.requestName) {
//                        "offer" -> handleOffer(it.data.newCall.data)
//                        "answer" -> handleAnswer(it.data.newCall.data)
//                        "ice" -> handleIce(it.data.newCall.data)
//                    }
//                }
//            } catch (e: ApolloException) {
//            }
//        }
    }

    private fun handleOffer(data: JsonReader) {
        val sdp = SessionDescription(SessionDescription.Type.OFFER, data.nextString())
        remotePeer?.setRemoteDescription(null, sdp)
        createAnswer()
    }

    private fun handleAnswer(data: JsonReader) {
        val sdp = SessionDescription(SessionDescription.Type.ANSWER, data.nextString())
        localPeer?.setRemoteDescription(null, sdp)
    }

    private fun handleIce(data: JsonReader) {
        val iceCandidate = IceCandidate(
            data.nextString(),
            data.nextInt(),
            data.nextString()
        )
        localPeer?.addIceCandidate(iceCandidate)
    }

    private fun createAnswer() {
        localPeer?.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                localPeer?.setLocalDescription(null, sessionDescription)
                // Send SDP answer to signaling server
            }

            override fun onCreateFailure(error: String?) {}
            override fun onSetSuccess() {}
            override fun onSetFailure(error: String?) {}
        }, MediaConstraints())
    }

    private fun endCall() {
//        videoTrack?.dispose()
//        videoSource?.dispose()
//        peerConnectionFactory.dispose()
//        eglBase?.release()
    }

//    private fun initWebRTC() {
//        eglBase = EglBase.create()
//        val options = PeerConnectionFactory.InitializationOptions.builder(requireActivity())
//            .setEnableInternalTracer(true)
//            .createInitializationOptions()
//        PeerConnectionFactory.initialize(options)
//
//        val audioDeviceModule =
//            JavaAudioDeviceModule.builder(requireContext()).createAudioDeviceModule()
//
//        val factoryOptions = PeerConnectionFactory.Options()
//        peerConnectionFactory = PeerConnectionFactory.builder()
//            .setOptions(factoryOptions)
//            .setAudioDeviceModule(audioDeviceModule)
//            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase?.eglBaseContext, true, true))
//            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase?.eglBaseContext))
//            .createPeerConnectionFactory()
//
//        localVideoView?.init(eglBase?.eglBaseContext, object : RendererCommon.RendererEvents {
//            override fun onFirstFrameRendered() {
//            }
//
//            override fun onFrameResolutionChanged(width: Int, height: Int, fps: Int) {

//            }
//        })
//        localVideoView?.setMirror(true)
//        remoteVideoView?.init(eglBase?.eglBaseContext, null)
//
//        startLocalMedia()
//    }
//
//    private fun startLocalMedia() {
//        val videoCapturer = createCameraCapturer()
//        videoSource = peerConnectionFactory.createVideoSource(false)
//
//        val surfaceTextureHelper =
//            SurfaceTextureHelper.create("CaptureThread", eglBase?.eglBaseContext)
//
//        videoCapturer?.initialize(
//            surfaceTextureHelper,
//            requireActivity(),
//            videoSource?.capturerObserver
//        )
//        videoCapturer?.startCapture(1280, 720, 30)
//
//        videoTrack = peerConnectionFactory.createVideoTrack("video", videoSource)
//        localVideoView?.visibility = SurfaceViewRenderer.VISIBLE
//        videoTrack?.addSink(localVideoView)
//
//        val audioConstraints = MediaConstraints()
//        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
//        audioTrack = peerConnectionFactory.createAudioTrack("audio", audioSource)
//
//        setupPeerConnections()
//    }
//
//    private fun createCameraCapturer(): VideoCapturer? {
//        val cameraEnumerator = Camera2Enumerator(requireContext())
//        for (deviceName in cameraEnumerator.deviceNames) {
//            if (cameraEnumerator.isFrontFacing(deviceName)) {
//                return cameraEnumerator.createCapturer(deviceName, null)
//            }
//        }
//        for (deviceName in cameraEnumerator.deviceNames) {
//            if (!cameraEnumerator.isFrontFacing(deviceName)) {
//                return cameraEnumerator.createCapturer(deviceName, null)
//            }
//        }
//        return null
//    }
//
//    private fun getIceServers(): List<PeerConnection.IceServer> {
//        return listOf(
//            // STUN server
//            PeerConnection.IceServer.builder("stun:eu-turn4.xirsys.com").createIceServer(),
//
//            // TURN server
//            PeerConnection.IceServer.builder(
//                listOf(
//                    "turn:eu-turn4.xirsys.com:80?transport=udp",
//                    "turn:eu-turn4.xirsys.com:3478?transport=tcp"
//                )
//            )
//                .setUsername("ml0jh0qMKZKd9P_9C0UIBY2G0nSQMCFBUXGlk6IXDJf8G2uiCymg9WwbEJTMwVeiAAAAAF2__hNSaW5vbGVl")
//                .setPassword("4dd454a6-feee-11e9-b185-6adcafebbb45")
//                .createIceServer()
//        )
//    }
//
//    private fun setupPeerConnections() {
//        val iceServers = getIceServers()
//
//        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
//        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
//
//        localPeerConnection =
//            peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
//                override fun onSignalingChange(newState: PeerConnection.SignalingState) {}
//                override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {}
//                override fun onIceConnectionReceivingChange(p0: Boolean) {
//
//                }
//
//                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
//
//                }
//
//                override fun onIceCandidate(candidate: IceCandidate) {
//                    remotePeerConnection?.addIceCandidate(candidate)
//                }
//
//                override fun onTrack(transceiver: RtpTransceiver) {
//                    val remoteTrack = transceiver.receiver.track() as VideoTrack
//                    remoteTrack.addSink(remoteVideoView)
//                }
//
//                override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
//                override fun onAddStream(stream: MediaStream) {
//                    stream.videoTracks?.firstOrNull()?.addSink(remoteVideoView)
//                }
//
//                override fun onRemoveStream(stream: MediaStream) {}
//                override fun onDataChannel(dataChannel: DataChannel) {}
//                override fun onRenegotiationNeeded() {}
//            })!!
//
//        remotePeerConnection =
//            peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
//                override fun onSignalingChange(newState: PeerConnection.SignalingState) {}
//                override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {}
//                override fun onIceConnectionReceivingChange(p0: Boolean) {
//
//                }
//
//                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
//
//                }
//
//                override fun onIceCandidate(candidate: IceCandidate) {
//                    localPeerConnection?.addIceCandidate(candidate)
//                }
//
//                override fun onTrack(transceiver: RtpTransceiver) {}
//                override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
//                override fun onAddStream(stream: MediaStream) {}
//                override fun onRemoveStream(stream: MediaStream) {}
//                override fun onDataChannel(dataChannel: DataChannel) {}
//                override fun onRenegotiationNeeded() {}
//            })!!
//    }

    private fun updateGiftIcon(gender: Int) {
        when (gender) {
            0 -> binding?.ivGiftIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.yellow_gift_male, null
                )
            )

            1 -> binding?.ivGiftIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.pink_gift_noavb, null
                )
            )

            else -> binding?.ivGiftIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.purple_gift_nosay, null
                )
            )
        }
    }

    private fun openMenuItem() {
//        val popup = PopupMenu(
//            requireContext(),
//            binding?.actionReportMes!!,
//            10,
//            R.attr.popupMenuStyle,
//            R.style.PopupMenu2
//        )
        val popup = PopupMenu(
            requireContext(),
            binding?.actionReportMes!!,
            10
        )
        popup.menuInflater.inflate(R.menu.search_profile_options, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.nav_item_report -> {
                    reportDialog()
                }

                R.id.nav_item_block -> {
                    blockUserAlert()
                }
            }
            true
        }
        popup.show()
    }

    private fun blockUserAlert() {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete, null)
        val headerTitle = dialogLayout.findViewById<TextView>(R.id.header_title)
        val noButton = dialogLayout.findViewById<TextView>(R.id.no_button)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)

        headerTitle.text =
            "${AppStringConstant1.are_you_sure_you_want_to_block} $otherFirstName ?"
        noButton.text = "${AppStringConstant(requireActivity()).no}"
        yesButton.text = "${AppStringConstant(requireActivity()).yes}"

        val builder = AlertDialog.Builder(activity, R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            dialog.dismiss()
            blockAccount()
        }

        dialog.show()
    }

    private fun reportDialog() {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
//        val reportView = dialogLayout.findViewById<TextView>(R.id.report_view)
        val reportMessage = dialogLayout.findViewById<EditText>(R.id.report_message)
        val okButton = dialogLayout.findViewById<TextView>(R.id.ok_button)
        val cancleButton = dialogLayout.findViewById<TextView>(R.id.cancel_button)
//        reportView.text = "${AppStringConstant(getMainActivity()).are_you_sure_you_want_to_delete_story}"
        okButton.text = "${AppStringConstant(requireActivity()).ok}"
        cancleButton.text = "${AppStringConstant(requireActivity()).cancel}"

        val builder = AlertDialog.Builder(activity, R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()

        okButton.setOnClickListener {
            val message = reportMessage.text.toString()
            reportAccount(otherUserId, message)
            dialog.dismiss()
        }

        cancleButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun blockAccount() {
        lifecycleScope.launch(Dispatchers.Main) {
            when (val response = viewModel.blockUser(userId, otherUserId, token = userToken)) {
                is Resource.Success -> {
                    viewModel.getCurrentUser(userId!!, userToken!!, true)
                    hideProgressView()
                    binding?.root?.snackbar("${otherFirstName} ${AppStringConstant1.blocked}")

                    (activity as MainActivity).pref.edit()?.putString("chatListRefresh", "true")
                        ?.putString("readCount", "false")?.apply()
                    findNavController().popBackStack()
                }

                is Resource.Error -> {
                    hideProgressView()
                    Log.e(TAG, "${getString(R.string.something_went_wrong)} ${response.message}")
                    binding?.root?.snackbar("${AppStringConstant1.something_went_wrong} ${response.message}")
                }

                else -> {
                }
            }
        }
    }

    private fun reportAccount(otherUserId: String?, reasonMsg: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            reportUserAccount(
                token = userToken,
                currentUserId = userId,
                otherUserId = otherUserId,
                reasonMsg = reasonMsg,
                mViewModel = viewModel
            ) { message ->
                hideProgressView()
                binding?.root?.snackbar(message)
            }
        }
    }

    private fun gotoChatUserProfile() {
        val bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", giftUserid)

        findNavController().navigate(
            destinationId = R.id.action_global_otherUserProfileFragment,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }


    private fun initInputListener() {
        binding?.input?.setInputListener { input ->
            // if (!binding!!.coinsCounter.text.toString().equals("0"))
            if (!isMessageSending) sendMessageToServer(input.toString())
            binding?.input?.inputEditText?.hideKeyboard()
            binding?.input?.inputEditText?.setText("")
            return@setInputListener false
        }

        binding?.input?.inputEditText?.setOnFocusChangeListener { v, hasFocus ->
            if (binding?.includeAttached?.clAttachments?.visibility == View.VISIBLE) {
                binding?.includeAttached?.clAttachments?.visibility = View.GONE
            }
        }

        binding?.input?.inputEditText?.setOnFocusChangeListener { v, hasFocus ->
            if (binding?.includeAttached?.clAttachments?.visibility == View.VISIBLE) {
                binding?.includeAttached?.clAttachments?.visibility = View.GONE
            }
        }

        binding?.input?.setInputTextListener { inputText ->
            if (inputText.isNotEmpty()) {
                binding?.recordButton?.visibility = View.GONE
                setViewMargins(
                    binding?.input!!, 0, 0, 0, 0
                )
            } else {
                binding?.recordButton?.visibility = View.VISIBLE
                setViewMargins(
                    binding?.input!!, 0, 0, convertPXtoDP(50, requireContext()), 0
                )
            }
        }

        binding?.input?.setAttachmentsListener {
            if (binding?.includeAttached?.clAttachments?.visibility == View.VISIBLE) {
                binding?.includeAttached?.clAttachments?.visibility = View.GONE
            } else {
                binding?.includeAttached?.clAttachments?.visibility = View.VISIBLE
                binding?.includeAttached?.llContacts?.visibility = View.VISIBLE
                binding?.includeAttached?.llCamera?.setOnClickListener {
                    val intent = Intent(requireActivity(), CameraActivity::class.java)
                    intent.putExtra("video_duration_limit", 60)
                    intent.putExtra("withCrop", false)
                    photosLauncher.launch(intent)
                    binding?.includeAttached?.clAttachments?.visibility = View.GONE
                }

                binding?.includeAttached?.llGallery?.setOnClickListener {
                    val intent = Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    )
                    galleryImageLauncher.launch(
                        intent
                    )
                    binding?.includeAttached?.clAttachments?.visibility = View.GONE
                }

                binding?.includeAttached?.llLocation?.setOnClickListener {
                    shareLocation()
                    Log.e(TAG, "Location")
                    binding?.includeAttached?.clAttachments?.visibility = View.GONE
                }

                binding?.includeAttached?.llContacts?.setOnClickListener {
                    binding?.includeAttached?.clAttachments?.visibility = View.GONE
                    val intent = Intent(requireContext(), ContactActivity::class.java).apply {
                        putExtra("isInviteFriendsLink", false)
                    }
                    getContactActivityLauncher.launch(intent)
                }
            }
        }

        binding?.input?.setGiftButtonListener {
            if (binding?.includeAttached?.clAttachments?.visibility == View.VISIBLE) {
                binding?.includeAttached?.clAttachments?.visibility = View.GONE
            }
            if (giftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                giftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                giftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        giftbottomSheetBehavior = BottomSheetBehavior.from(binding?.giftbottomSheet!!)
        giftbottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        binding?.sendgiftto?.setOnClickListener {
            isProgressShow = true
            val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
            fragVirtualGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            fragRealGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            lifecycleScope.launchWhenCreated {
                if (items.size > 0) {
                    if (isProgressShow) {
                        showProgressView()
                    }
                    items.forEach { gift ->
                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        try {
                            res = apolloClient(requireContext(), userToken!!).mutation(
                                GiftPurchaseMutation(
                                    gift.id,
                                    giftUserid!!,
                                    getCurrentUserId()!!
                                )
                            ).execute()
                        } catch (e: ApolloException) {
                            Toast.makeText(
                                requireContext(),
                                "Exception ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e(TAG, "res --> ${Gson().toJson(res)}")
                        if (res?.hasErrors() == false) {
                            Toast.makeText(
                                requireContext(),
                                AppStringConstant1.you_bought + " ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (res!!.hasErrors()) {
                            Toast.makeText(
                                requireContext(),
                                "${res.errors!![0].message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e(
                            TAG,
                            "apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}"
                        )
                    }


                    // notifyAdapter(serverDataList as ArrayList<GetChatMessagesByRoomIdQuery.Edge?>?)
                    getCurrentUserDetails()
                    // getChatMessages()
                    hideProgressView()
                    giftbottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        binding?.giftsTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding?.giftsPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        binding?.giftsTabs?.setupWithViewPager(binding?.giftsPager)
        setupViewPager(binding?.giftsPager)
    }


    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragRealGifts = FragmentRealGifts()
        fragVirtualGifts = FragmentVirtualGifts()

        adapter.addFragItem(fragRealGifts!!, AppStringConstant1.real_gifts)
        adapter.addFragItem(fragVirtualGifts!!, AppStringConstant1.virtual_gifts)
        viewPager?.adapter = adapter
    }

    fun fireGiftBuyNotificationforreceiver(gid: String, userid: String?) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val queryName = "sendNotification"
                val query = StringBuilder().append("mutation {").append("$queryName (")
                    .append("userId: \"${userid}\", ").append("notificationSetting: \"GIFT\", ")
                    .append("data: {giftId:${gid}}").append(") {").append("sent").append("}")
                    .append("}").toString()
                val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                    query, queryName, userToken
                )
            }
        }
    }

    private fun scrollToBottom() {
        binding?.rvChatMessages?.layoutManager?.scrollToPosition(0)
    }

    private fun notifyAdapter(
        edges2: ArrayList<GetChatMessagesByRoomIdQuery.Edge?>?
    ) {
        if (edges2?.size!! > 0) {
            chatAdapter.updateList(edges2 as ArrayList<GetChatMessagesByRoomIdQuery.Edge>)
        }
        Handler(Looper.getMainLooper()).postDelayed({ hideProgressView() }, 100)
    }

    private fun isOtherUserOnline() {
        lifecycleScope.launch {
            try {
                val id = requireArguments().getString("otherUserId")
                val res =
                    apolloClient(requireContext(), userToken!!).query(IsOnlineQuery(id!!))
                        .execute()
                if (!res.hasErrors()) {
                    binding?.otherUserOnlineStatus?.visibility =
                        if (res.data?.isOnline?.isOnline == true) View.VISIBLE else View.GONE
                }
            } catch (e: ApolloException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFirstMessages() {
        lifecycleScope.launchWhenCreated {
            try {
                val res = apolloClient(requireContext(), userToken!!).query(
                    GetFirstMessageListQuery()
                ).execute()
                val datas = res.data!!.firstmessageMsgs!!.edges
                datas.forEach { Edge ->
                    val msg = GetChatMessagesByRoomIdQuery.Edge(
                        GetChatMessagesByRoomIdQuery.Node(
                            id = Edge!!.node!!.byUserId.id!!,
                            content = Edge.node!!.content,
                            //appLanguageCode =Edge.node.byUserId.userLanguageCode.toString(),
                            roomId = GetChatMessagesByRoomIdQuery.RoomId(
                                id = "", name = ""
                            ),
                            timestamp = Edge.node.timestamp,
                            userId = GetChatMessagesByRoomIdQuery.UserId(
                                id = Edge.node.byUserId.id,
                                username = Edge.node.byUserId.username,
                                avatarIndex = Edge.node.byUserId.avatarIndex,
                                null
                            ),
                            messageType = MessageMessageType.C,
                            privatePhotoRequestId = 0,
                            requestStatus = ""
                        )
                    )
                    serverDataList.addAll(checkForImageVideoInMessage(msg))
                }
                if (!res.hasErrors()) {
                    Log.e(TAG, "apolloResponse success ${serverDataList.size}")
                    notifyAdapter(serverDataList as ArrayList<GetChatMessagesByRoomIdQuery.Edge?>?)
                } else {
                    Log.e(TAG, "apolloResponse error ${res.errors?.get(0)?.message}")
                }
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                return@launchWhenCreated
            }
        }
    }

    private fun getBrodcastMessages() {
        lifecycleScope.launch {
            try {
                val res = apolloClient(requireContext(), userToken!!).query(
                    GetBroadcastMessageListQuery()
                ).execute()

                val datas = res.data!!.broadcastMsgs!!.edges
                datas.forEach { Edge ->
                    val msg = GetChatMessagesByRoomIdQuery.Edge(
                        GetChatMessagesByRoomIdQuery.Node(
                            id = Edge!!.node!!.byUserId.id!!,
                            content = Edge.node!!.content,
                            roomId = GetChatMessagesByRoomIdQuery.RoomId(
                                id = "", name = ""
                            ),
                            timestamp = Edge.node.timestamp,
                            userId = GetChatMessagesByRoomIdQuery.UserId(
                                id = Edge.node.byUserId.id,
                                username = Edge.node.byUserId.username,
                                avatarIndex = Edge.node.byUserId.avatarIndex,
                                null
                            ),
                            messageType = MessageMessageType.C,
                            privatePhotoRequestId = 0,
                            requestStatus = ""
                        )
                    )
                    serverDataList.addAll(checkForImageVideoInMessage(msg))
                }

                if (!res.hasErrors()) {
                    Log.e(TAG, "apolloResponse success ${serverDataList.size}")
                    notifyAdapter(serverDataList as ArrayList<GetChatMessagesByRoomIdQuery.Edge?>?)
                } else {
                    Log.e(TAG, "apolloResponse error ${res.errors?.get(0)?.message}")
                }
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                return@launch
            }
        }
    }

    override fun onChatMessageDelete(message: GetChatMessagesByRoomIdQuery.Edge?) {
        lifecycleScope.launch {
            try {
                val response = apolloClient(requireContext(), userToken!!).mutation(
                    DeleteUserMessagesMutation(message?.node?.id.toString())
                ).execute()
                if (response.hasErrors()) {
                    val errors = response.errors?.get(0)?.message
                    Toast.makeText(requireContext(), "$errors", Toast.LENGTH_LONG).show()
                } else {
                    hideProgressView()
                }
            } catch (e: ApolloException) {
                hideProgressView()
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                hideProgressView()
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPrivatePhotoAccessResult(decision: String, requestId: Int) {
        lifecycleScope.launch {
            try {
                val response = apolloClient(requireContext(), userToken!!).mutation(
                    PrivatePhotosDecisionMutation(decision, requestId)
                ).execute()

                if (response.hasErrors()) {
                    val errors = response.errors?.get(0)?.message
                    Toast.makeText(requireContext(), "$errors", Toast.LENGTH_LONG).show()
                } else {

                    if (decision == "A") {
                        binding?.root?.snackbar(AppStringConstant1.you_accepted_the_request)
                    } else {
                        binding?.root?.snackbar(AppStringConstant1.you_reject_the_request)
                    }
                    updateCoinView()
                }
            } catch (e: ApolloException) {
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateLastSeen(roomId: Int) {
        lifecycleScope.launch {
            try {
                val response = apolloClient(requireContext(), userToken!!).mutation(
                    GetLastSeenMessageMutation(roomId)
                ).execute()

                if (response.hasErrors()) {
                    Log.e(TAG, "${response.errors} ")
                    val errors = response.errors?.get(0)?.message
                    Toast.makeText(requireContext(), "$errors", Toast.LENGTH_LONG).show()
                } else {
                    Log.e(TAG, "updateLastSeen ")
                }

            } catch (e: ApolloException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getLastSeenMessageId(roomId: Int) {
        lifecycleScope.launch {
            try {
                val response = apolloClient(requireContext(), userToken!!).query(
                    GetLastSeenMessageUserQuery(roomId)
                ).execute()
                if (response.hasErrors()) {
                    Log.e(TAG, "${response.errors}")
                    val errors = response.errors?.get(0)?.message
                    Toast.makeText(requireContext(), "$errors", Toast.LENGTH_LONG).show()
                } else {
                    chatAdapter.lastSeenMessageId =
                        response.data?.lastSeenMessageUser?.id.toString()
                    val edge =
                        serverDataList.filter { it?.node?.id == response.data?.lastSeenMessageUser?.id.toString() }
                    CoroutineScope(Dispatchers.Main).launch {
                        if (edge.isNotEmpty()) {
                            chatAdapter.notifyItemChanged(serverDataList.indexOf(edge[0]))
                        }
                    }
                }
            } catch (e: ApolloException) {
                Toast.makeText(
                    requireContext(),
                    "UpdateLastSeen " + e.message,
                    Toast.LENGTH_LONG
                )
                    .show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkForImageVideoInMessage(item: GetChatMessagesByRoomIdQuery.Edge?): ArrayList<GetChatMessagesByRoomIdQuery.Edge?> {
        if (item?.node?.messageType != MessageMessageType.G) {
            val content = item?.node?.content
            if (content?.contains("media/chat_files") == true) {
                if (content.contains(" ")) {
                    val link = content.substring(0, content.indexOf(" "))
                    val message = content.substring(content.indexOf(" ") + 1)
                    val newNode = item.node.copy(content = message)
                    val textItem = item.copy(node = newNode)
                    val newNodeMedia = item.node.copy(content = link)
                    val linkItem = item.copy(node = newNodeMedia)
                    return arrayListOf(linkItem, textItem)
                }
            }
        }
        return arrayListOf(item)
    }

    private fun getChatMessages() {
        val activity: Activity? = activity
        if (activity != null) {
            lifecycleScope.launchWhenStarted {
                try {
                    val query = GetChatMessagesByRoomIdQuery(
                        roomId.toString(), 40, endCursor
                    )
                    Log.e(
                        TAG,
                        "--- qry = getChatMessages = " + {
                            query.toString().getGraphqlApiBody()
                        } + " n -------${query}")
                    val res = apolloClient(requireActivity(), userToken!!).query(
                        query
                    ).execute()
                    if (!res.hasErrors()) {
                        hideProgressView()
                        serverDataList.clear()
                        Log.e(TAG, "apolloResponse chat ${res.data?.messages}")

                        res.data?.messages?.edges?.forEach { edge ->
                            serverDataList.addAll(checkForImageVideoInMessage(edge))
                        }
                        endCursor = res.data?.messages?.pageInfo?.endCursor ?: ""
                        hasNextPage = res.data?.messages!!.pageInfo.hasNextPage
                        isUpdatesChatView = false
                        notifyAdapter(
                            serverDataList as ArrayList<GetChatMessagesByRoomIdQuery.Edge?>?
                        )
                    } else {
                        hideProgressView()
                        Log.e(TAG, "apolloResponse error ${res.errors?.get(0)?.message}")
                    }

                } catch (e: ApolloException) {
                    hideProgressView()
                    Log.e(TAG, "apolloResponse ${e.message}")
                    return@launchWhenStarted
                }
            }
        }
    }

    private fun sendMessageToServer(input: String?) {
        isProgressShow = true
        isMessageSending = true
        lifecycleScope.launch {
            send(input!!)
        }
    }

    suspend fun send(input: String) {
        Log.e(TAG, "--- chatID = ${roomId} n -------${input}")
        val requ = SendChatMessageMutation(
            input, roomId
        )
        Log.e(
            TAG,
            "--- qry = send = " + { requ.toString().getGraphqlApiBody() } + " n -------${requ}")

        val res = apolloClient(requireActivity(), userToken!!).mutation(
            requ
        ).execute()

        Log.e(TAG, "--- res = ${res} n -------${input}")

        if (!res.hasErrors()) {
            res.data
            getCurrentUserDetails()
            isMessageSending = false
        } else {
            copyToClipboard(requireActivity(), res.errors!![0].message)
            if (res.errors!![0].message.contains("Not enough coins")) {
                binding?.root?.snackbar(
                    res.errors!![0].message,
                    Snackbar.LENGTH_INDEFINITE,
                    callback = {
                        findNavController().navigate(
                            destinationId = R.id.actionGoToPurchaseFragment,
                            popUpFragId = null,
                            animType = AnimationTypes.SLIDE_ANIM,
                            inclusive = true,
                        )
                    })
            } else {
                Log.e(TAG, "--- errror = ${res.errors!![0].message} n -------${input}")
                val message = res.data?.sendMessage?.message
                if (message != null) {
                    val edge = GetChatMessagesByRoomIdQuery.Edge(
                        node = GetChatMessagesByRoomIdQuery.Node(
                            id = roomId.toString(),
                            content = message.content,
                            timestamp = message.timestamp,
                            messageType = MessageMessageType.C,
                            privatePhotoRequestId = null,
                            requestStatus = null,
                            roomId = GetChatMessagesByRoomIdQuery.RoomId(
                                id = roomId.toString(),
                                name = "Room $roomId"
                            ),
                            userId = GetChatMessagesByRoomIdQuery.UserId(
                                id = null,
                                username = message.userId.username,
                                avatarIndex = 0,
                                avatarPhotos = null
                            )
                        )
                    )
                    serverDataList.add(edge)

                    notifyAdapter(serverDataList as ArrayList<GetChatMessagesByRoomIdQuery.Edge?>?)

                    getChatMessages()
                }
                Toast.makeText(
                    requireContext(), "" + res.errors!![0].message, Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding?.input?.inputEditText?.text = null
        isMessageSending = false
    }

    override fun onChatMessageClick(
        position: Int,
        message: GetChatMessagesByRoomIdQuery.Edge?
    ) {
        val url = message?.node?.content
        if (url?.contains("media/chat_files") == true) {
            var fullUrl = url

            if (fullUrl.contains(" ")) {
                val link = fullUrl.substring(0, fullUrl.indexOf(" "))
                val giftMessage = fullUrl.substring(fullUrl.indexOf(" ") + 1)
                fullUrl = link
            } else {
                if (url.startsWith("/media/chat_files/")) {
                    fullUrl = "${BuildConfig.BASE_URL}$url"
                }
            }

            val uri = Uri.parse(fullUrl)
            val lastSegment = uri.lastPathSegment
            val ext = lastSegment?.substring(lastSegment.lastIndexOf(".") + 1)
            //This is audio file
            if (ext.contentEquals("3gp")) {
                return
            }
            if (ext.contentEquals("jpg") || ext.contentEquals("png") || ext.contentEquals("jpeg")) {
                binding?.imgUserStory1?.visibility = View.VISIBLE
                if (showBottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    showBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding?.imgUserStory1?.loadImage(fullUrl)

                } else {
                    showBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            } else {
                VideoPlayerActivity.startVideoPlayer(requireActivity(), fullUrl)
            }
        }
    }

    override fun onChatUserAvtarClick() {
        gotoChatUserProfile()
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("gift_Received")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.registerReceiver(
                broadCastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            activity?.registerReceiver(broadCastReceiver, intentFilter)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            kotlin.run {
                if (MainActivity.getMainActivity() != null) {
                    val notificationManager = MainActivity.getMainActivity()!!
                        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancelAll()
                }
            }
        }, 1000)
    }

    private fun subscribeOnNewMessage() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnNewMessageSubscription(roomId, userToken!!, "")
                ).toFlow().catch {
                    it.printStackTrace()
                    subscribeOnDeleteMessage()
                }.retryWhen { cause, attempt ->
                    delay(attempt * 1000)
                    true
                }.collect { newMessage ->
                    if (newMessage.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newMessage.errors?.get(0)?.message}"
                        )
                    } else {
                        Log.e(TAG, "_realtime = ${newMessage.data?.onNewMessage?.message?.content}")

                        if (newMessage.data?.onNewMessage?.message != null) {
                            val avatarPhotos =
                                serverDataList.find { it?.node?.userId?.id == newMessage.data?.onNewMessage?.message?.userId?.id }?.node?.userId?.avatarPhotos
                            newMessage.data?.onNewMessage?.message?.let { message ->
                                val edge = GetChatMessagesByRoomIdQuery.Edge(
                                    GetChatMessagesByRoomIdQuery.Node(
                                        id = message.id,
                                        content = message.content,
                                        //    appLanguageCode =message.appLanguageCode,
                                        roomId = GetChatMessagesByRoomIdQuery.RoomId(
                                            id = message.roomId.id,
                                            name = message.roomId.name
                                        ),
                                        timestamp = message.timestamp,
                                        userId = GetChatMessagesByRoomIdQuery.UserId(
                                            id = message.userId.id,
                                            username = message.userId.username,
                                            avatarIndex = message.userId.avatarIndex,
                                            avatarPhotos = avatarPhotos
                                        ),
                                        messageType = message.messageType,
                                        privatePhotoRequestId = message.privatePhotoRequestId,
                                        requestStatus = message.requestStatus
                                    )
                                )

                                val adapterList = chatAdapter.currentList.toMutableList()
                                adapterList.add(0, edge)
                                runOnUiThread {
                                    chatAdapter.submitList(adapterList)
                                }
                                updateLastSeen(roomId)
                            }
                        } else {
                            lifecycleScope.launch {
                                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                    endCursor = ""
                                    hasNextPage = false
                                    updateCoinView()
                                }
                            }
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    private fun subscribeOnDeleteMessage() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnDeleteMessageSubscription(roomId!!, userToken!!, "")
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newMessage ->
                    if (newMessage.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newMessage.errors?.get(0)?.message}"
                        )
                    } else {
                        val adapterList = chatAdapter.currentList.toMutableList()
                        val index =
                            adapterList.indexOfFirst { it?.node?.id?.toInt() == newMessage.data?.onDeleteMessage?.id }
                        adapterList.removeAt(index)
                        chatAdapter.submitList(adapterList.toList())
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    private fun subscribeOnLastSeenMessage() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    LastSeenMessageByReceiverSubscription(userToken!!, roomId!!)
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newMessage ->
                    if (!newMessage.hasErrors()) {
                        Log.e(TAG, "realetimeLastSeenException")
                        val edge =
                            serverDataList.find { it?.node?.id == newMessage.data?.onSeenLastMessageByReceiver?.message?.id }
                        var previousPosition = 0
                        if (edge != null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                chatAdapter.notifyItemChanged(previousPosition)
                                chatAdapter.notifyItemChanged(serverDataList.indexOf(edge))
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                chatAdapter.notifyItemChanged(previousPosition)
                            }
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    private
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (intent!!.action.equals("gift_Received")) {
                val extras = intent.extras
                val state = extras!!.getString("extra")

                Log.e(TAG, "_gift_received" + state)
            }
        }
    }

    private fun updateCoinView() {
        showProgressView()
        getChatMessages()
        getCurrentUserDetails()
    }

    private fun showErrorInLocation(jsonObject: Resource<ResponseBody<JsonObject>>) {
        if (jsonObject.message != null && jsonObject.message.contains("Not enough coins..")) {
            binding?.root?.snackbar(
                AppStringConstant1.no_enough_coins,
                Snackbar.LENGTH_INDEFINITE,
                callback = {
                    findNavController().navigate(
                        destinationId = R.id.actionGoToPurchaseFragment,
                        popUpFragId = null,
                        animType = AnimationTypes.SLIDE_ANIM,
                        inclusive = true,
                    )
                })
        } else {
            getCurrentUserDetails()
        }
    }

    private fun shareCurrentLocation() {
        val locationService =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationService.lastLocation.addOnSuccessListener { location: Location? ->
            var lat: Double? = location?.latitude
            var lon: Double? = location?.longitude
            if (lat != null && lon != null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.shareLocation(
                        "$lat,$lon",
                        roomId,
                        "bb2bb0d9-9c84-44ce-889c-7707c1cd7387",
                        userToken!!
                    ).let { data -> showErrorInLocation(data) }
                }
            } else {
                val priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                val cancellationTokenSource = CancellationTokenSource()
                locationService.getCurrentLocation(
                    priority,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location == null) binding?.root?.snackbar(
                        AppStringConstant1.location_enable_message,
                        Snackbar.LENGTH_INDEFINITE,
                        callback = {
                            shareLocation()
                        })
                    else {
                        lat = location.latitude
                        lon = location.longitude
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.shareLocation(
                                "$lat,$lon",
                                roomId!!,
                                "bb2bb0d9-9c84-44ce-889c-7707c1cd7387",
                                userToken!!
                            ).let { data -> showErrorInLocation(data) }
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Oops location failed with exception: $exception")
                }
            }
        }
    }

    private fun shareLocation() {
        if (hasPermissions(requireContext(), locPermissions)) {
            if (isLocationEnabled(requireContext())) {
                shareCurrentLocation()
            } else {
                enableLocation()
            }

        } else {
            permissionReqLauncher.launch(locPermissions)
        }
    }

    private
    val microPhonePermission = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    private fun enableLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30 * 1000.toLong()
            fastestInterval = 5 * 1000.toLong()
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse =
                    it.getResult(ApiException::class.java)
                if (response.locationSettingsStates?.isGpsPresent == true) {
                    shareLocation()
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest =
                            e.status.resolution?.let { it1 ->
                                IntentSenderRequest.Builder(it1).build()
                            }
                        launcher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
            }
        }.addOnCanceledListener {}
    }


    private val getContactActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getParcelableExtra<Contact>("contact")
                Toast.makeText(requireContext(), "Result: $data", Toast.LENGTH_SHORT).show()
            }
        }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                shareLocation()
            } else {
                binding?.root?.snackbar(AppStringConstant1.location_enable_message,
                    Snackbar.LENGTH_INDEFINITE,
                    callback = {})
            }
        }

    private fun subscribeonUpdatePrivatePhotoRequest() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(
                    requireActivity(),
                    userToken!!
                ).subscription(
                    OnUpdatePrivateRequestSubscription()
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newMessage ->
                    if (newMessage.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${
                                newMessage.errors?.get(
                                    0
                                )?.message
                            }"
                        )
                    } else {
                        Log.e(
                            TAG,
                            "reealltime onNewMessage ${newMessage.data?.onUpdatePrivateRequest?.requestedUser}"
                        )
                        lifecycleScope.launch {
                            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                updateCoinView()
                            }
                        }
                    }
                }
                Log.e(TAG, "reealltime 2")
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "reealltime exception= ${e2.message}")
            }
        }
    }


    private val locPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val photosLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val result =
                    data?.getStringExtra("result")
                Log.e(TAG, "PhotosLauncher result $result")
                if (result != null) {
                    var mediaType: String =
                        "video"
                    if (File(result).getMimeType()
                            .toString()
                            .startsWith("image")
                    ) {
                        mediaType = "image"
                    } else {
                        mediaType = "video"
                    }

                    Log.e(TAG, "UserToken $userToken")
                    UploadUtility(this@MessengerNewChatFragment).uploadFile(
                        result,
                        authorization = userToken,
                        upload_type = mediaType
                    ) { url ->
                        Log.e(TAG, "ReponseUrl: $url")
                        if (url.equals("url")) {
                            binding?.root?.snackbar(
                                AppStringConstant1.no_enough_coins,
                                Snackbar.LENGTH_INDEFINITE,
                                callback = {
                                    findNavController().navigate(
                                        destinationId = R.id.actionGoToPurchaseFragment,
                                        popUpFragId = null,
                                        animType = AnimationTypes.SLIDE_ANIM,
                                        inclusive = true,
                                    )
                                })
                        } else {
                            var input = url
                            if (url?.startsWith(
                                    "/media/chat_files/"
                                ) == true
                            ) {
                                input =
                                    "${BuildConfig.BASE_URL}$url"
                            }
                            sendMessageToServer(
                                input
                            )
                        }
                    }
                }
            }
        }

    private val galleryImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            val data =
                activityResult.data
            if (activityResult.resultCode == Activity.RESULT_OK) {
//                val result = data?.data?.path
//                val type = if (result?.contains("video") == true) ".mp4" else ".jpg"
//                val outputFile =
//                    requireContext().filesDir.resolve("${System.currentTimeMillis()}$type")

                val result =
                    data?.data?.path
                val openInputStream =
                    requireActivity().contentResolver?.openInputStream(
                        data?.data!!
                    )
                val type =
                    if (result?.contains(
                            "video"
                        ) == true
                    ) ".mp4" else ".jpg"
                val outputFile =
                    requireContext().filesDir.resolve(
                        "${System.currentTimeMillis()}$type"
                    )
                openInputStream?.copyTo(
                    outputFile.outputStream()
                )

                if (result != null) {
                    var file = File(
                        outputFile.toURI()
                    )
                    val mediaType =
                        if (file.getMimeType()
                                .toString()
                                .startsWith(
                                    "image"
                                )
                        ) {
                            "image"
                        } else {
                            "video"
                        }

                    if (mediaType == "image") {
                        val imagePath = convertURITOBitmapNSaveImage(
                            requireContext(),
                            data.data!!,
                            outputFile,
                        )

                        imagePath?.let {
                            file = File(it)
                        }
                    }

                    UploadUtility(
                        this@MessengerNewChatFragment
                    ).uploadFile2(
                        file,
                        authorization = userToken,
                        upload_type = mediaType
                    ) { url ->
                        Log.e(TAG, "galleryImageLauncher: responseurll $url")
                        if (url.equals(
                                "url"
                            )
                        ) {
                            binding?.root?.snackbar(
                                AppStringConstant1.no_enough_coins,
                                Snackbar.LENGTH_INDEFINITE,
                                callback = {
                                    findNavController().navigate(
                                        destinationId = R.id.actionGoToPurchaseFragment,
                                        popUpFragId = null,
                                        animType = AnimationTypes.SLIDE_ANIM,
                                        inclusive = true,
                                    )
                                })
                        } else {
                            Log.e(
                                TAG,
                                "galleryImageLauncher: responseurll input $url"
                            )
                            var input =
                                url
                            if (url?.startsWith(
                                    "/media/chat_files/"
                                ) == true
                            ) {
                                input =
                                    "${BuildConfig.BASE_URL}$url"
                            }
                            sendMessageToServer(
                                input
                            )
                        }
                    }
                }
            }
        }

    private val permissionAudioRecorder =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            val granted =
                permission.entries.all {
                    it.value == true
                }
            if (granted) {
//                binding?.recordButton?.visibility = View.VISIBLE
//                binding?.recordView?.visibility = View.GONE
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission not granted",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val permissionReqLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            run {
                val granted =
                    permission.entries.all {
                        it.value == true
                    }
                if (granted) {
                    val locationService =
                        LocationServices.getFusedLocationProviderClient(
                            requireContext()
                        )
                    locationService.lastLocation.addOnSuccessListener { location: Location? ->
                        val lat: Double? =
                            location?.latitude

                        val lon: Double? =
                            location?.longitude
                        if (lat != null && lon != null) {
                            lifecycleScope.launch(
                                Dispatchers.Main
                            ) {
//                                val roomId = arguments?.getInt("chatId")
                                viewModel.shareLocation(
                                    "$lat,$lon",
                                    roomId!!,
                                    "bb2bb0d9-9c84-44ce-889c-7707c1cd7387",
                                    userToken!!
                                )
                                    .let { data ->
                                        showErrorInLocation(
                                            data
                                        )
                                    }
                            }
                        }
                    }
                }
            }
        }

    private suspend
    fun cancelChatRoom() {
        Log.e(TAG, "reealltime detached 3")
        deferred.cancel()
        Log.e(TAG, "reealltime detached 4")
        try {
            val result = deferred.await()
            Log.e(TAG, "reealltime detached 5")
        } catch (e: CancellationException) {
            Log.e(TAG, "reealltime cancel room exception ${e.message}")
        } finally {
            Log.e(TAG, "reealltime detached 6")
        }
    }

    private fun showImageDialog(width: Int, height: Int, imageUrl: String?) {
        val dialog = Dialog(requireContext())
        val dialogBinding = AlertFullImageBinding.inflate(layoutInflater, null, false)
        dialogBinding.fullImg.loadImage(imageUrl!!, {
            dialogBinding.alertTitle.setViewGone()
        }, {
            dialogBinding.alertTitle.text = it?.message
        })
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(width, height)
        dialog.show()
        dialogBinding.root.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun setupOtherUserData() {
        giftUserid = arguments?.getString("otherUserId")
        binding?.userName?.text = requireArguments().getString("otherUserName")
        binding?.sendgiftto?.text =
            AppStringConstant1.send_git_to + " " + requireArguments().getString(
                "otherUserName"
            )
        val url = requireArguments().getString("otherUserPhoto")
        binding?.userProfileImg?.loadCircleImage(url!!)
        binding?.closeBtn?.setOnClickListener {
            moveUp()
        }
        isOtherUserOnline()
        binding?.userProfileImg?.setOnClickListener {
            gotoChatUserProfile()
        }
        binding?.userName?.setOnClickListener {
            gotoChatUserProfile()
        }
        val gender = requireArguments().getInt("otherUserGender", 0)
        binding?.input?.updateGiftIcon(gender)
    }

    override fun onPause() {
        if (::chatAdapter.isInitialized)
            if (chatAdapter != null)
                chatAdapter.releaseMediaPlayer()
        super.onPause()
    }
}
