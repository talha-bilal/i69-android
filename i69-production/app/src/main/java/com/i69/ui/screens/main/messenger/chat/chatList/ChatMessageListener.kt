package com.i69.ui.screens.main.messenger.chat.chatList

import com.i69.GetChatMessagesByRoomIdQuery


interface ChatMessageListener {
    fun onChatMessageClick(position: Int, message: GetChatMessagesByRoomIdQuery.Edge?)
    fun onChatUserAvtarClick()
    fun onChatMessageDelete(message: GetChatMessagesByRoomIdQuery.Edge?)
    fun onPrivatePhotoAccessResult(decision: String, requestId: Int)
}