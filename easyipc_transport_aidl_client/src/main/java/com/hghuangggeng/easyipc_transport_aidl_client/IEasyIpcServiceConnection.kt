package com.hghuangggeng.easyipc_transport_aidl_client

import android.content.ServiceConnection

interface IEasyIpcServiceConnection : ServiceConnection {
    fun invoke(requestData: ByteArray?) : ByteArray?
    fun destroy()
}