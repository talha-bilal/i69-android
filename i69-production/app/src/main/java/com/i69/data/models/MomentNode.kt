package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class MomentNode(
    var pk: Int? = null,
    var comment: Int? = null,
    var createdDate: Any? = null,
    var publishAt: Any? = null,
    var `file`: String? = null,
    var id: String? = null,
    var like: Int? = null,
    var momentDescription: String? = null,
    var momentDescriptionPaginated: List<String?>? = null,
    var image: ByteArray? = null
)
