package com.hghuangggeng.easyipc_transport_aidl_client

import android.content.ServiceConnection
import com.hghuangggeng.easyipc_transport_aidl.IEasyIpcCallback

interface IEasyIpcServiceConnection : ServiceConnection {
    fun invoke(requestData: ByteArray?) : ByteArray?
    fun asyncInvoke(requestData: ByteArray?, callback: IEasyIpcCallback?)
}