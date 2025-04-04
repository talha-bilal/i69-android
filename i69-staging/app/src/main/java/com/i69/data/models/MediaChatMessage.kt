package com.i69.data.models

import androidx.annotation.Keep
import com.stfalcon.chatkit.commons.models.MessageContentType

@Keep
class MediaChatMessage(user: User?, timeStampMillis: Long, private val path: String) :
    ChatMessage(user, timeStampMillis, ""), MessageContentType.Image {
    override fun getImageUrl(): String = path
}