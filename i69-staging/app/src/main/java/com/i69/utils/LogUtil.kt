package com.i69.utils

import android.util.Log

object LogUtil {

    private const val TAG = "I69Logs"

    fun debug(msg: String?) {
        if (msg != null) {
            Log.e(TAG, msg)
        }
    }
}