package com.hghuangggeng.easyipc_transport_contentprovider_client

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class EasyIpcAsyncCallbackHandler :
    Handler(Looper.getMainLooper()) {
    private val pendingCallbacks =
        ConcurrentHashMap<String, WeakReference<IEasyIpcRawCallback>>() // key:异步requestId, val:IEasyIpcRawCallback

    fun registerCallback(requestId: String, listener: IEasyIpcRawCallback?) {
        pendingCallbacks[requestId] = WeakReference(listener)
    }

    fun removeAllCallback() {
        pendingCallbacks.clear()
    }

    fun unregisterAndGetCallback(requestId: String) = pendingCallbacks.remove(requestId)?.get()

    override fun handleMessage(msg: Message) {
        val requestId = msg.data.getString("requestId")
        if (requestId.isNullOrEmpty()) {
            return
        }

        unregisterAndGetCallback(requestId)?.onCallback(
            msg.data.getByteArray("resultData"),
            requestId
        )
            ?: run {
                Log.w(TAG, "Callback not found or already recycled for ID: $requestId")
            }
    }

    companion object {
        private const val TAG = "EasyIpcAsyncCallbackHandler"
    }
}