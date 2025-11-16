package com.hghuangggeng.easyipc_transport_aidl

import android.content.Context
import android.os.RemoteCallbackList
import android.util.Log
import com.hghuangggeng.easyipc_core.BaseEasyIpcServer
import com.hghuangggeng.easyipc_core.IEasyIpcServer

class EasyIpcBinder(context: Context) : IEasyIpcService.Stub() {

    private val remoteCallbackList = RemoteCallbackList<IEasyIpcCallback>()
    private val easyIpcServer: IEasyIpcServer = BaseEasyIpcServer(context)

    override fun registerCallback(callback: IEasyIpcCallback?) {
        remoteCallbackList.register(callback).also {
            Log.i(TAG, "registerCallback:$it")
        }
    }

    override fun unregisterCallback(callback: IEasyIpcCallback?) {
        remoteCallbackList.unregister(callback).also {
            Log.i(TAG, "unregisterCallback:$it")

        }
    }

    override fun invoke(requestData: ByteArray?): ByteArray {
        return easyIpcServer.onInvoke(requestData)
    }

    companion object {
        private const val TAG = "EasyIpcBinder"
    }
}