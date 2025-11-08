package com.hghuangggeng.easyipc_transport_aidl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallResult<T : Parcelable>(val data: T?, val status: Boolean) : Parcelable
