package com.i69.ui.interfaces

import android.content.DialogInterface

interface AlertDialogCallback {
    fun onNegativeButtonClick(dialog: DialogInterface)
    fun onPositiveButtonClick(dialog: DialogInterface)
}