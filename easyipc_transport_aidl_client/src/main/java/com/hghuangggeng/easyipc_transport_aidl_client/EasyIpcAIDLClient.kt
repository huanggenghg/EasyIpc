package com.hghuangggeng.easyipc_transport_aidl_client

import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.hghuangggeng.easyipc_baseclient.BaseEasyIpcClient
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import com.hghuangggeng.easyipc_transport_aidl.IEasyIpcCallback
import javax.inject.Inject

class EasyIpcAIDLClient @Inject constructor() : BaseEasyIpcClient(), DefaultLifecycleObserver {
    private var connection: EasyIpcServiceConnection? = null

    override fun start(context: Context, serverPackage: String, lifecycle: Lifecycle?) {
        lifecycle?.removeObserver(this)
        lifecycle?.addObserver(this)
        val intent = Intent()
        intent.action = SERVICE_ACTION_NAME
        intent.setPackage(serverPackage)
        connection = EasyIpcServiceConnection()
        context.bindService(intent, connection!!, BIND_AUTO_CREATE).also {
            Log.i(TAG, "bindService:$it")
        }
    }

    override fun invoke(requestData: ByteArray?): ByteArray? {
        return connection?.invoke(requestData)
    }

    override fun asyncInvoke(
        requestData: ByteArray?,
        requestId: String,
        callback: IEasyIpcRawCallback?
    ) {
        connection?.asyncInvoke(requestData, object : IEasyIpcCallback.Stub() {
            override fun onCallback(data: ByteArray?) {
                callback?.onCallback(data, requestId)
            }
        })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        connection?.let {
            (owner as? Context)?.unbindService(it)
        }
    }

    companion object {
        private const val TAG = "EasyIpcAIDLServer"
        private const val SERVICE_ACTION_NAME =
            "com.hghuangggeng.easyipc_transport_aidl.EasyIpcService"
    }
}