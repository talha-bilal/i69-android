package com.i69.data.remote.requests

import androidx.annotation.Keep

@Keep
data class ReportRequest(
    val reportee: String?,
    val reporter: String?,
    val timestamp: String?
)