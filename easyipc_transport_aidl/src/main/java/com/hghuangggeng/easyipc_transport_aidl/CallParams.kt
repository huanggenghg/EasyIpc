package com.hghuangggeng.easyipc_transport_aidl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallParams<T : Parcelable>(val funcName: String, val params: T, val isAsync: Boolean) :
    Parcelable
