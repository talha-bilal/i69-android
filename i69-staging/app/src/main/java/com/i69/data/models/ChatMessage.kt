package com.i69.data.models

import androidx.annotation.Keep
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.Date

@Keep
open class ChatMessage(
    private val user: User?,
    private val timeStampMillis: Long,
    private val msg: String
) : IMessage {

    companion object {
        private var counter = 0
    }

    override fun getId() = (counter++).toString()

    override fun getCreatedAt(): Date {
        return Date().apply {
            time = timeStampMillis
        }
    }

    override fun getText(): String = msg

    override fun getUser(): IUser = object : IUser {

        override fun getAvatar(): String = user?.avatarPhotos!![user.avatarIndex!!].url ?: ""

        override fun getName(): String? {
            val fullName = user?.fullName
            val name = if (fullName != null && fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }
            return name
        }

        override fun getId(): String? = user?.id

    }
}