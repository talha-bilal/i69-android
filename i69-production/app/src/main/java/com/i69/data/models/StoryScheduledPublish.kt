package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class StoryScheduledPublish(
    val isPublished: Boolean? = null,
    val publishAt: String? = null
)