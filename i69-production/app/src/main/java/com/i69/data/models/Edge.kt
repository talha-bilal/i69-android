package com.i69.data.models

import androidx.annotation.Keep
import com.i69.type.UserGender

@Keep
data class Edge(
    /**
     * A cursor for use in pagination
     */
    val cursor: String,
    /**
     * The item at the end of the edge
     */
    val node: Node?,
)

@Keep
data class Node(
    val pk: Int?,
    val comment: Int?,
    val createdDate: Any,
    val `file`: String?,
    /**
     * The ID of the object.
     */
    val id: String,
    val like: Int?,
    val momentDescription: String,
    val momentDescriptionPaginated: List<String?>?,
    val user: User2?,
)

@Keep
data class User2(
    val id: Any,
    val email: String,
    val fullName: String,
    /**
     * Required. 150 characters or fewer. Letters, digits and @/./+/-/_ only.
     */
    val username: String,
    val gender: UserGender?,
    val avatar: Avatar?,
    val onesignalPlayerId: String?,
    val avatarPhotos: List<AvatarPhoto>,
)

@Keep
 data class Avatar(
    val url: String?,
    val id: String,
    val user: String?,
)

@Keep
 data class AvatarPhoto(
    val url: String?,
    val id: String,
    val user: String?,
)
