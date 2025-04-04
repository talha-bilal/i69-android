package com.i69.ui.screens.main.messenger.chat.contact

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val contactName: String,
    val phoneNumber: String,
    val email: String?,
    val notes: String?,
    val country: String?
) : Parcelable