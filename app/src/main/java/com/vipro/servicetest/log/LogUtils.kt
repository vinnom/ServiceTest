package com.vipro.servicetest.log

import android.util.Log

const val TAG = "ServiceTest"

fun logd(
    subTag: String = "",
    message: () -> String,
) {
    if (subTag.isNotEmpty()) Log.d(TAG, "$subTag - ${message()}")
    else Log.d(TAG, message())
}