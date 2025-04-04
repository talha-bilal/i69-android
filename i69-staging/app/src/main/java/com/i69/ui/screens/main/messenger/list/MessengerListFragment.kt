package com.i69.ui.screens.main.messenger.list


import android.app.AlertDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.GetAllRoomsQuery
import com.i69.GetBroadcastMessageQuery
import com.i69.GetFirstMessageQuery
import com.i69.GetparticularRoomsQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.MessageQuery
import com.i69.databinding.FragmentMessengerListBinding
import com.i69.databinding.ItemRequestPreviewLongBinding
import com.i69.firebasenotification.NotificationBroadcast
import com.i69.type.MessageMessageType
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.messenger.list.MessengerListAdapter.MessagesListListener
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.LogUtil
import com.i69.utils.Resource
import com.i69.utils.animateFromLeft
import com.i69.utils.apolloClient
import com.i69.utils.defaultAnimate
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.setVisibleOrInvisible
import com.i69.utils.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class MessengerListFragment : BaseFragment<FragmentMessengerListBinding>(), MessagesListListener {

    private lateinit var job: Job
    private var dataFetchJob: Job? = null

    private var firstMessage: GetAllRoomsQuery.Edge? = null
    private var broadcastMessage: GetAllRoomsQuery.Edge? = null
    private var allRoomMessages: MutableList<MessageQuery> = mutableListOf()
    private var TAG: String = MessengerListFragment::class.java.simpleName
    private var isRunning = false
    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var messengerListAdapter: MessengerListAdapter
    var endCursor: String = ""
    var hasNextPage: Boolean = false
    private var userId: String? = null
    private var userToken: String? = null
    lateinit var handler: Handler
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMessengerListBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    fun performSearch(query: String): List<MessageQuery> {
        val lowercaseQuery = query.lowercase(Locale.getDefault())
        return allRoomMessages.filter { it ->
            it.edge?.node?.target?.fullName?.lowercase(Locale.getDefault())
                ?.contains(lowercaseQuery) == true || it.edge?.node?.userId?.fullName?.lowercase(
                Locale.getDefault()
            )?.contains(lowercaseQuery) == true
        }
    }

    override fun setupTheme() {
        getTypeActivity<MainActivity>()?.reloadNavigationMenu()
        navController = findNavController()

        binding?.chatSearch1?.setOnClickListener {
            binding?.chatSearch1?.visibility = View.GONE
            binding?.chatSearch?.visibility = View.VISIBLE
            binding?.chatSearch?.isIconified = false
        }

        binding?.chatSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredRooms = performSearch(newText)
                if(messengerListAdapter != null)
                    messengerListAdapter.updateList(filteredRooms)
                return true
            }
        })

        binding?.chatSearch?.setOnCloseListener {
            var allRoomMessagestemp: MutableList<MessageQuery> = ArrayList()
            messengerListAdapter.updateList(allRoomMessagestemp)
            messengerListAdapter.updateList(allRoomMessages)
            binding?.chatSearch1?.visibility = View.VISIBLE
            binding?.chatSearch?.visibility = View.GONE
            true // Return true to indicate that you've handled the event.
        }

        viewStringConstModel.data.observe(this@MessengerListFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }

        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!

            messengerListAdapter = MessengerListAdapter(this@MessengerListFragment, userId)
            binding?.messengerList?.setItemAnimator(null)
            binding?.messengerList?.adapter = messengerListAdapter

            updateList(true)
        }


        handler.postDelayed(object : Runnable {
            override fun run() {
                checkForUpdates()
                messengerListAdapter.notifyDataSetChanged()
                handler.postDelayed(this, 50 * 1000)
            }
        }, 50 * 1000)


        getTypeActivity<MainActivity>()?.enableNavigationDrawer()


        if (getMainActivity().pref.getString("chatListRefresh", "false").equals("true")) {
            getMainActivity().pref.edit()?.putString("chatListRefresh", "false")?.apply()
            allRoomMessages.clear()
        }
        isRunning = false
        lifecycleScope.launch {
            viewModel.shouldUpdateAdapter.collect {
                Log.e(TAG, "Collecting Data: Update ($it)")
            }
        }
        try {
            job = lifecycleScope.launch {
                viewModel.newMessageFlow.collect { message ->
                    message?.let { newMessage ->
                        Log.e(TAG, "NewMessage:  $newMessage")
                        try {
                            val index = allRoomMessages.indexOfFirst {
                                it.edge?.node?.id == newMessage.roomId.id
                            }
                            val selectedRoom = allRoomMessages[index]
                            val room = GetAllRoomsQuery.Edge(
                                GetAllRoomsQuery.Node(
                                    id = selectedRoom.edge?.node?.id!!,
                                    name = selectedRoom.edge.node.name,
                                    lastModified = newMessage.timestamp,
                                    blocked = 0,
                                    unread = selectedRoom.edge.node.unread?.toInt()?.plus(1)
                                        ?.toString(),
                                    messageSet = GetAllRoomsQuery.MessageSet(edges = selectedRoom.edge.node.messageSet.edges.toMutableList()
                                        .apply {
                                            set(
                                                0, GetAllRoomsQuery.Edge1(
                                                    GetAllRoomsQuery.Node1(
                                                        content = newMessage.content,
                                                        id = newMessage.id,
                                                        roomId = GetAllRoomsQuery.RoomId(id = newMessage.roomId.id),
                                                        timestamp = newMessage.timestamp,
                                                        messageType = newMessage.messageType,
                                                        read = newMessage.read
                                                    )
                                                )
                                            )
                                        }),
                                    userId = selectedRoom.edge.node.userId,
                                    target = selectedRoom.edge.node.target,
                                )
                            )
                            val edge = MessageQuery(room)
                            allRoomMessages.set(index = index, edge)
                            Log.e(TAG, "submitList1" + "202")
                            messengerListAdapter.submitList1(allRoomMessages)
                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        checkForUpdates()
    }

    fun onNewMessage(roomId: String) {
        Log.e(TAG, "onNewMessage: RoomId: $roomId")
        val position = allRoomMessages.indexOfFirst { it.edge?.node?.id == roomId }

        Log.e(TAG, "onNewMessage: position: $position")
        if (position != 0) {
            getParticularRoomForUpdate(position, roomId, true)
        } else {
            Log.e(TAG, "onNewMessage: Position is 0")
            getParticularRoomForUpdate(-1, roomId, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val arguments = arguments
        if (arguments != null) {
            val roomID = arguments.get("roomIDNotify") as String?
            if (roomID != null) {
                getParticularRoom(roomID)
                arguments.run {
                    remove("roomIDNotify")
                    clear()
                }
            }
        }
        handler = Handler(Looper.getMainLooper())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun checkForUpdates() {


        if (getMainActivity().pref.getString("roomIDNotify", "false").equals("true")) {
            LogUtil.debug("Check for updates In roomIDNotify")
            getMainActivity().pref.edit().putString("roomIDNotify", "false").apply()
            getParticularRoom(getMainActivity().pref.getString("roomID", ""))
        }
        if (getMainActivity().pref.getString("readCount", "false").equals("false")) {
            LogUtil.debug("Check for updates In readCount is false")
            if (getMainActivity().pref.getString("newChat", "false").equals("true")) {
                LogUtil.debug("Check for updates In newChat")
                getMainActivity().pref.edit().putString("newChat", "false").apply()
            }
        }
        if (getMainActivity().pref.getString("readCount", "false").equals("true")) {
            LogUtil.debug("Check for updates In readCount is true")
            getMainActivity().pref.edit().putString("readCount", "false").apply()
            if (getMainActivity().pref.getString("type", "").equals("001")) {
                LogUtil.debug("Check for updates In readCount type is 001")
                val position = getMainActivity().pref.getInt("position", 0)
                val id = getMainActivity().pref.getString("id", "")
                if (position > 0) {
                    if (allRoomMessages.size != 0) getParticularFirstMessageUpdate(position, id!!)
                }
            } else if (getMainActivity().pref.getString("type", "").equals("000")) {
                LogUtil.debug("Check for updates In readCount type is 000")
                val position = getMainActivity().pref.getInt("position", 0)
                val id = getMainActivity().pref.getString("id", "")
                if (allRoomMessages.size != 0) getParticularBraodCastUpdate(position, id!!)
            } else {
                LogUtil.debug("Check for updates In readCount type is else")
                val position = getMainActivity().pref.getInt("position", 0)
                val id = getMainActivity().pref.getString("id", "")
                if (position > 0) {
                    if (allRoomMessages.size != 0) getParticularRoomForUpdate(
                        position = position,
                        id!!
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        allRoomMessages.clear()


        getMainActivity().setDrawerItemCheckedUnchecked(null)
    }

    fun updateView(state: String) {
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val extras = intent?.extras
            val state = extras!!.getString("extra")
            Log.e("TAG_Notification_rece", "onReceive: $state")
            updateView(state.toString())
        }
    }

    override fun onPause() {
        if (this::handler.isInitialized) {
            handler.removeCallbacksAndMessages(null)
        }
        super.onPause()
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener { (activity as MainActivity).drawerSwitchState() }
        binding?.goToSearchBtn?.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onDestroyView() {
        if (this::handler.isInitialized) {
            handler.removeCallbacksAndMessages(null)
        }
        super.onDestroyView()
    }

    private fun makePreviewAnimation() {
        binding?.goToSearchBtn?.setViewVisible()
        binding?.messengerListPreview?.setViewVisible()
        binding?.messengerList?.setVisibleOrInvisible(false)
        binding?.messengerListPreview?.setViewVisible()
        val display = requireActivity().windowManager!!.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        binding?.subTitle?.defaultAnimate(100, 200)
        setupPreviewItem(binding?.firstAnimPreview, R.drawable.icon_boy)
        setupPreviewItem(binding?.secondAnimPreview, R.drawable.icon_girl)
        setupPreviewItem(binding?.thirdAnimPreview, R.drawable.icon_girl_2)
        binding?.firstAnimPreview?.root?.animateFromLeft(200, 300, metrics.widthPixels / 3)
        binding?.secondAnimPreview?.root?.animateFromLeft(200, 500, metrics.widthPixels / 3)
        binding?.thirdAnimPreview?.root?.animateFromLeft(200, 700, metrics.widthPixels / 3)
    }

    private fun setupPreviewItem(
        requestPreviewBinding: ItemRequestPreviewLongBinding?, preview: Int
    ) {
        requestPreviewBinding?.previewImg?.setImageResource(preview)
    }

    private fun getParticularRoom(roomID: String?) {
        Log.e(TAG, "ROOM_ID= $roomID")
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(
                        requireContext(),
                        userToken!!
                    ).query(GetparticularRoomsQuery(roomID!!))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    stopShimmerEffect()
                    return@repeatOnLifecycle
                }
                stopShimmerEffect()
                val Rooms = res.data?.room
                val chatBundle = Bundle()
                if (Rooms?.userId!!.id.equals(userId)) {
                    chatBundle.putString("otherUserId", Rooms.target.id)
                    if (Rooms.target.avatar != null) {
                        chatBundle.putString("otherUserPhoto", Rooms.target.avatar.url ?: "")
                    } else {
                        chatBundle.putString("otherUserPhoto", "")
                    }
                    val fullName = Rooms.target.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    chatBundle.putString("otherUserName", name)
                } else {
                    chatBundle.putString("otherUserId", Rooms.userId.id)
                    if (Rooms.userId.avatar != null) {
                        chatBundle.putString("otherUserPhoto", Rooms.userId.avatar.url ?: "")
                    } else {
                        chatBundle.putString("otherUserPhoto", "")
                    }
                    val fullName = Rooms.userId.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    chatBundle.putString("otherUserName", name)

                }
                chatBundle.putInt("chatId", Rooms.id.toInt())
                findNavController().navigate(R.id.globalUserToNewChatAction, chatBundle)
            }
        }
    }

    private fun getParticularFirstMessageUpdate(position: Int, id: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val resFirstMessage = try {
                    apolloClient(requireContext(), userToken!!).query(GetFirstMessageQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    return@repeatOnLifecycle
                }
                if (resFirstMessage.hasErrors()) {
                    if (resFirstMessage.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        lifecycleScope.launch(Dispatchers.Main) {
                            userPreferences?.clear()
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
                if (resFirstMessage.data?.firstmessage != null) {
                    firstMessage = GetAllRoomsQuery.Edge(
                        GetAllRoomsQuery.Node(
                            id = "001",
                            name = resFirstMessage.data?.firstmessage?.firstmessageContent!!,
                            lastModified = resFirstMessage.data?.firstmessage?.firstmessageTimestamp,
                            unread = resFirstMessage.data?.firstmessage?.unread,
                            blocked = 0,
                            messageSet = GetAllRoomsQuery.MessageSet(edges = mutableListOf<GetAllRoomsQuery.Edge1>().apply {
                                add(
                                    GetAllRoomsQuery.Edge1(
                                        GetAllRoomsQuery.Node1(
                                            content = "",
                                            id = "001",
                                            roomId = GetAllRoomsQuery.RoomId(id = ""),
                                            timestamp = resFirstMessage.data?.firstmessage?.firstmessageTimestamp!!,
                                            read = "",
                                            messageType = MessageMessageType.C
                                        )
                                    )
                                )
                            }),
                            userId = GetAllRoomsQuery.UserId(
                                null,
                                resFirstMessage.data?.firstmessage?.firstmessageContent!!,
                                null,
                                null,
                                null,
                                null
                            ),
                            target = GetAllRoomsQuery.Target(null, null, null, null, null, null),
                        )
                    )
                }
                if (firstMessage != null) {
                    try {
                        allRoomMessages[position] = MessageQuery(firstMessage!!, true)
                        messengerListAdapter.submitList1(allRoomMessages)
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }
                getMainActivity().updateChatBadge()
            }
        }
    }

    private fun getParticularBraodCastUpdate(position: Int, id: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val resBroadcast = try {
                    apolloClient(requireContext(), userToken!!).query(GetBroadcastMessageQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponsegetBroadcastMessage ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    return@repeatOnLifecycle
                }
                if (resBroadcast.hasErrors()) {
                    if (resBroadcast.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        lifecycleScope.launch(Dispatchers.Main) {
                            userPreferences?.clear()
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
                if (resBroadcast.data?.broadcast != null) {
                    Log.e(TAG, "BroadcastMessage: ${resBroadcast.data?.broadcast}")
                    broadcastMessage = GetAllRoomsQuery.Edge(
                        GetAllRoomsQuery.Node(
                            id = "000",
                            name = resBroadcast.data?.broadcast?.broadcastContent!!,
                            lastModified = resBroadcast.data?.broadcast?.broadcastTimestamp,
                            unread = resBroadcast.data?.broadcast?.unread,
                            blocked = 0,
                            messageSet = GetAllRoomsQuery.MessageSet(edges = mutableListOf<GetAllRoomsQuery.Edge1>().apply {
                                add(
                                    GetAllRoomsQuery.Edge1(
                                        GetAllRoomsQuery.Node1(
                                            content = "",
                                            id = "000",
                                            roomId = GetAllRoomsQuery.RoomId(id = ""),
                                            timestamp = resBroadcast.data?.broadcast?.broadcastTimestamp!!,
                                            read = "",
                                            messageType = MessageMessageType.C
                                        )
                                    )
                                )
                            }),
                            userId = GetAllRoomsQuery.UserId(
                                null,
                                "Team i69",
                                null,
                                null,
                                null,
                                null
                            ),
                            target = GetAllRoomsQuery.Target(null, null, null, null, null, null),
                        )
                    )
                }
                if (broadcastMessage != null && allRoomMessages.size > position) {
                    allRoomMessages[position] = MessageQuery(broadcastMessage!!, true)
                    messengerListAdapter.submitList1(allRoomMessages)
                }
                getMainActivity().updateChatBadge()
            }
        }
    }

    private fun getParticularRoomForUpdate(
        position: Int,
        id: String,
        isFromNotification: Boolean = false
    ) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(GetAllRoomsQuery(20))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse get room ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    return@repeatOnLifecycle
                }
                if (position != -1) {
                    val room = res.data?.rooms?.edges
                    Log.e(TAG, "Id: $id")
                    val newMessage = room?.find { id == it?.node?.id }
                    Log.e(TAG, "NewMessage: $newMessage")
                    Log.e(TAG, "ChangeNewMessagePosition: $position")
                    val user = newMessage?.node?.name
                    val unreadCount = newMessage?.node?.unread
                    Log.e(TAG, "User: $user     UnreadCount: $unreadCount")
                    try {
                        allRoomMessages.forEach {
                            Log.e(TAG, it.toString())
                        }
                        val foundItem = allRoomMessages.find { it.edge?.node?.id == id }
                        Log.e(TAG, "Found Item: $foundItem")
                        if (foundItem != null) {
                            allRoomMessages.remove(foundItem)
                        } else {
                            allRoomMessages.removeAt(position)
                        }
                        allRoomMessages.add(0, MessageQuery(newMessage))
                        messengerListAdapter.submitList1(allRoomMessages)
                    } catch (e: Exception) {
                        Log.e(TAG, "Excpetion: $e")
                    }
                    getMainActivity().updateChatBadge()
                } else {

                    val gson = Gson()
                    val room = res.data?.rooms?.edges
                    Log.e(TAG, "Id: $id")
                    val newMessage = room?.find { id == it?.node?.id }
                    Log.e(TAG, "NewMessage: $newMessage")
                    Log.e(TAG, "ChangeNewMessagePosition: $position")
                    val user = newMessage?.node?.name
                    val unreadCount = newMessage?.node?.unread
                    Log.e(TAG, "User: $user     UnreadCount: $unreadCount")
                    try {
                        allRoomMessages.forEach {
                            Log.e(TAG, it.toString())
                        }
                        val foundItem = allRoomMessages.find { it.edge?.node?.id == id }
                        Log.e(TAG, "Found Item: $foundItem")
                        if (foundItem != null) {
                            allRoomMessages.remove(foundItem)
                        }
                        allRoomMessages.add(0, MessageQuery(newMessage))
                        messengerListAdapter.submitList1(allRoomMessages)
                    } catch (e: Exception) {
                        Log.e(TAG, "Excpetion: $e")
                    }
                    getMainActivity().updateChatBadge()
                }
                if (isFromNotification) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        kotlin.run {
                            val notificationManager =
                                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.cancelAll()
                        }
                    }, 1000)
                }
            }
        }
        NotificationBroadcast.isBroadcastReceived = false
    }

    private val formatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    fun updateList(isProgressShow: Boolean) {
        if (isProgressShow) {
            startShimmerEffect()
        }
        allRoomMessages.clear()
        dataFetchJob?.cancel()
        dataFetchJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val resBroadcast = try {
                    apolloClient(requireContext(), userToken!!).query(GetBroadcastMessageQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponsegetBroadcastMessage ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    stopShimmerEffect()
                    return@repeatOnLifecycle
                }
                if (resBroadcast.hasErrors()) {
                    if (resBroadcast.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        lifecycleScope.launch(Dispatchers.Main) {
                            userPreferences?.clear()
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
                if (resBroadcast.data?.broadcast != null) {
                    Log.e(TAG, "BroadcastMessage: ${resBroadcast.data?.broadcast}")
                    broadcastMessage = GetAllRoomsQuery.Edge(
                        GetAllRoomsQuery.Node(
                            id = "000",
                            name = resBroadcast.data?.broadcast?.broadcastContent!!,
                            lastModified = resBroadcast.data?.broadcast?.broadcastTimestamp,
                            unread = resBroadcast.data?.broadcast?.unread,
                            blocked = 0,
                            messageSet = GetAllRoomsQuery.MessageSet(edges = mutableListOf<GetAllRoomsQuery.Edge1>().apply {
                                add(
                                    GetAllRoomsQuery.Edge1(
                                        GetAllRoomsQuery.Node1(
                                            content = "",
                                            id = "000",
                                            roomId = GetAllRoomsQuery.RoomId(id = ""),
                                            timestamp = resBroadcast.data?.broadcast?.broadcastTimestamp!!,
                                            read = "",
                                            messageType = MessageMessageType.C
                                        )
                                    )
                                )
                            }),
                            userId = GetAllRoomsQuery.UserId(
                                null,
                                "Team i69",
                                null,
                                null,
                                null,
                                null
                            ),
                            target = GetAllRoomsQuery.Target(null, null, null, null, null, null),
                        )
                    )
                }
                if (broadcastMessage != null) {
                    allRoomMessages.add(0, MessageQuery(broadcastMessage, true))
                }

                val res = try {
                    apolloClient(requireContext(), userToken!!).query(GetAllRoomsQuery(20))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse all moments ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    stopShimmerEffect()
                    return@repeatOnLifecycle
                }
                if (res.hasErrors() && !res.errors.isNullOrEmpty()) {
                    if (!res.errors!![0].nonStandardFields.isNullOrEmpty() && res.errors!![0].nonStandardFields?.containsKey(
                            "code"
                        )!! && res.errors!![0].nonStandardFields?.get("code")
                            .toString() == "InvalidOrExpiredToken"
                    ) {

                        lifecycleScope.launch(Dispatchers.Main) {
                            userPreferences?.clear()

                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
                var temp: MessageQuery? = null
                if (allRoomMessages.isNotEmpty()) temp = allRoomMessages.removeAt(0)
                res.data?.rooms!!.edges.forEach {
                    Log.e(TAG, "AddedList: ${it?.node}")
                    allRoomMessages.add(MessageQuery(it))
                }
                allRoomMessages =
                    allRoomMessages.filter { it.edge?.node?.messageSet?.edges?.isNotEmpty() == true }
                        .toMutableList()
                allRoomMessages =
                    allRoomMessages.filter { it.edge?.node?.blocked == 0 }.toMutableList()
                allRoomMessages.sortWith { o1, o2 ->
                    try {
                        val date1 = o1.edge?.node?.lastModified?.toString()
                        val date2 = o2.edge?.node?.lastModified?.toString()
                        formatter.parse(date2)?.compareTo(formatter.parse(date1)) ?: 0
                    } catch (throwable: Throwable) {
                        0
                    }
                }
                if (temp != null) allRoomMessages.add(0, temp)
                val resFirstMessage = try {
                    apolloClient(requireContext(), userToken!!).query(GetFirstMessageQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse getFirstMessage ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    return@repeatOnLifecycle
                }
                if (resFirstMessage.hasErrors() && !resFirstMessage.errors.isNullOrEmpty()) {
                    if (!resFirstMessage.errors!![0].nonStandardFields.isNullOrEmpty() && resFirstMessage.errors!![0].nonStandardFields?.containsKey(
                            "code"
                        ) == true && resFirstMessage.errors!![0].nonStandardFields?.get("code")
                            .toString() == "InvalidOrExpiredToken"
                    ) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            userPreferences?.clear()
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
                if (resFirstMessage.data?.firstmessage != null) {
                    firstMessage = GetAllRoomsQuery.Edge(
                        GetAllRoomsQuery.Node(
                            id = "001",
                            name = resFirstMessage.data?.firstmessage?.firstmessageContent!!,
                            lastModified = resFirstMessage.data?.firstmessage?.firstmessageTimestamp,
                            unread = resFirstMessage.data?.firstmessage?.unread,
                            blocked = 0,
                            messageSet = GetAllRoomsQuery.MessageSet(edges = mutableListOf<GetAllRoomsQuery.Edge1>().apply {
                                add(
                                    GetAllRoomsQuery.Edge1(
                                        GetAllRoomsQuery.Node1(
                                            content = "",
                                            id = "001",
                                            roomId = GetAllRoomsQuery.RoomId(id = ""),
                                            timestamp = resFirstMessage.data?.firstmessage?.firstmessageTimestamp!!,
                                            read = "",
                                            messageType = MessageMessageType.C
                                        )
                                    )
                                )
                            }),
                            userId = GetAllRoomsQuery.UserId(
                                null,
                                resFirstMessage.data?.firstmessage?.firstmessageContent!!,
                                null,
                                null,
                                null,
                                null
                            ),
                            target = GetAllRoomsQuery.Target(null, null, null, null, null, null),
                        )
                    )
                    allRoomMessages.add(MessageQuery(firstMessage, true))
                    var temp: MessageQuery? = null
                    if (allRoomMessages.isNotEmpty()) temp = allRoomMessages.removeAt(0)
                    allRoomMessages.sortWith { o1, o2 ->
                        try {
                            val date1 = o1.edge?.node?.lastModified?.toString()
                            val date2 = o2.edge?.node?.lastModified?.toString()
                            formatter.parse(date2)?.compareTo(formatter.parse(date1)) ?: 0
                        } catch (throwable: Throwable) {
                            0
                        }
                    }
                    if (temp != null) allRoomMessages.add(0, temp)
                }
                stopShimmerEffect()
                messengerListAdapter.submitList1(allRoomMessages)

                if (allRoomMessages.size != 0) {
                    if (res.data?.rooms?.pageInfo?.endCursor != null) {
                        endCursor = res.data?.rooms!!.pageInfo.endCursor!!
                        hasNextPage = res.data?.rooms!!.pageInfo.hasNextPage
                    }
                }
                var totoalunread = 0
                allRoomMessages.indices.forEach { i ->
                    val data = allRoomMessages[i]
                    totoalunread =
                        if (totoalunread == 0 && data.edge?.node!!.unread!!.toInt() > 0) {
                            1
                        } else {
                            if (totoalunread > 0 && data.edge?.node!!.unread!!.toInt() > 0) {
                                totoalunread + 1
                            } else totoalunread
                        }
                }
                try {
                    getMainActivity().binding.bottomNavigation.addBadge(totoalunread)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            kotlin.run {
                val notificationManager =
                    requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        }, 1000)

    }

    private fun stopShimmerEffect() {
        TransitionManager.beginDelayedTransition(binding?.clRoot, AutoTransition())
        binding?.shimmer?.apply {
            stopShimmer()
            setViewGone()
            binding?.messengerList?.setViewVisible()
        }
    }

    private fun startShimmerEffect() {
        binding?.shimmer?.apply {
            setViewVisible()
            startShimmer()
            binding?.messengerList?.setViewGone()
        }
    }

    override fun onItemClick(query: MessageQuery, position: Int) {
        viewModel.setSelectedMessagePreview(query.edge!!)
        val chatBundle = Bundle()
        LogUtil.debug("NodeId : : ${query.edge.node?.id}")
        when (query.edge.node?.id) {
            "001" -> {
                if (query.edge.node.unread?.toInt()!! > 0) {
                    getMainActivity().pref.edit().putString("readCount", "true")
                        .putString("type", "001").putString("id", query.edge.node.id)
                        .putInt("position", position).apply()
                }
                chatBundle.putString("otherUserId", "")
                chatBundle.putString("otherUserPhoto", "")
                val fullName = query.edge.node.userId.fullName
                val name = if (fullName != null && fullName.length > 15) {
                    fullName.substring(0, minOf(fullName.length, 15))
                } else {
                    fullName
                }
                chatBundle.putString("otherUserName", name)
                chatBundle.putInt("otherUserGender", 0)
                chatBundle.putString("ChatType", "001")
                chatBundle.putInt("chatId", 0)
                findNavController().navigate(R.id.globalUserToNewChatAction, chatBundle)
            }

            "000" -> {
                if (query.edge.node.unread?.toInt()!! > 0) {
                    getMainActivity().pref.edit().putString("readCount", "true")
                        .putString("type", "000").putString("id", query.edge.node.id)
                        .putInt("position", position).apply()
                }
                chatBundle.putString("otherUserId", "")
                chatBundle.putString("otherUserPhoto", "")
                val fullName = query.edge.node.userId.fullName
                val name = if (fullName != null && fullName.length > 15) {
                    fullName.substring(0, minOf(fullName.length, 15))
                } else {
                    fullName
                }
                chatBundle.putString("otherUserName", name)
                chatBundle.putInt("otherUserGender", 0)
                chatBundle.putString("ChatType", "000")
                chatBundle.putInt("chatId", 0)
                findNavController().navigate(R.id.globalUserToNewChatAction, chatBundle)
            }

            else -> {
                if (query.edge.node?.unread?.toInt()!! > 0) {
                    getMainActivity().pref.edit().putString("readCount", "true")
                        .putString("type", "User").putString("id", query.edge.node.id)
                        .putInt("position", position).apply()
                }
                if (query.edge.node.userId.id.equals(userId)) {
                    chatBundle.putString("otherUserId", query.edge.node.target.id)
                    if (query.edge.node.target.avatar != null) {
                        chatBundle.putString(
                            "otherUserPhoto", query.edge.node.target.avatar.url ?: ""
                        )
                    } else {
                        chatBundle.putString("otherUserPhoto", "")
                    }
                    val fullName = query.edge.node.target.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    chatBundle.putString("otherUserName", name)
                    chatBundle.putInt("otherUserGender", query.edge.node.target.gender ?: 0)
                    chatBundle.putString("ChatType", "Normal")
                } else {
                    chatBundle.putString("otherUserId", query.edge.node.userId.id)
                    if (query.edge.node.userId.avatar != null) {
                        chatBundle.putString(
                            "otherUserPhoto", query.edge.node.userId.avatar.url ?: ""
                        )
                    } else {
                        chatBundle.putString("otherUserPhoto", "")
                    }
                    val fullName = query.edge.node.userId.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    chatBundle.putString("otherUserName", name)
                    chatBundle.putInt("otherUserGender", query.edge.node.userId.gender ?: 0)
                    chatBundle.putString("ChatType", "Normal")
                }
                chatBundle.putInt("chatId", query.edge.node.id.toInt())
                findNavController().navigate(R.id.globalUserToNewChatAction, chatBundle)
            }
        }
    }

    override fun onItemDeleteClicked(roomId: String, position: Int) {
        Log.e(TAG, "onItemDeleteClicked: $roomId")
        showDeleteConfirmationDialog(roomId)
    }

    private fun showDeleteConfirmationDialog(roomId: String) {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete, null)
        val headerTitle = dialogLayout.findViewById<TextView>(R.id.header_title)
        val noButton = dialogLayout.findViewById<TextView>(R.id.no_button)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)
        val title = "Are you sure you want to delete?"

        headerTitle.text = title
        noButton.text = AppStringConstant(requireContext()).no
        yesButton.text = AppStringConstant(requireContext()).yes

        val builder = AlertDialog.Builder(MainActivity.getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            dialog.dismiss()
            if (roomId == "000" || roomId == "001") {
                Toast.makeText(requireContext(), "RoomId: $roomId", Toast.LENGTH_LONG).show()
            } else {
                deleteChatRoom(roomId)
            }
        }

        dialog.show()
    }

    private fun deleteChatRoom(roomId: String) {
        if (roomId.isNotEmpty()) {
            startShimmerEffect()
            viewModel.deleteChatRoom(roomId.toInt(), userToken.toString()) {
                Log.e(TAG, "onItemDeleteClicked: ${it.message}")
                stopShimmerEffect()
                when (it) {
                    is Resource.Success -> {
                        Log.e(TAG, "onItemDeleteClicked: ${it.data?.data?.message}")
                        updateList(true)
                    }

                    is Resource.Error -> {

                    }

                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    fun getMainActivity() = activity as MainActivity

}
