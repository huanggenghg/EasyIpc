package com.hghuangggeng.easyipc_transport_aidl

import android.content.Context
import com.hghuangggeng.easyipc_baseserver.BaseEasyIpcServer
import com.hghuangggeng.easyipc_baseserver.IEasyIpcServer
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback

class EasyIpcBinder(context: Context) : IEasyIpcService.Stub() {
    private val easyIpcServer: IEasyIpcServer = BaseEasyIpcServer(context)

    override fun invoke(requestData: ByteArray?): ByteArray {
        return easyIpcServer.onInvoke(requestData)
    }

    override fun asyncInvoke(
        requestData: ByteArray?,
        callback: IEasyIpcCallback?
    ) {
        easyIpcServer.onAsyncInvoke(requestData, object : IEasyIpcRawCallback {
            override fun onCallback(data: ByteArray?) {
                callback?.onCallback(data)
            }
        })
    }

    companion object {
        private const val TAG = "EasyIpcBinder"
    }
}