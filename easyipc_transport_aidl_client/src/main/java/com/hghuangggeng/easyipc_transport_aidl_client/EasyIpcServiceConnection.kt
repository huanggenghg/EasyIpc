package com.hghuangggeng.easyipc_transport_aidl_client

import android.content.ComponentName
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.hghuangggeng.easyipc_transport_aidl.IEasyIpcCallback
import com.hghuangggeng.easyipc_transport_aidl.IEasyIpcService

class EasyIpcServiceConnection : IEasyIpcCallback.Stub(), IEasyIpcServiceConnection, IBinder.DeathRecipient {

    private var easyIpcService: IEasyIpcService?  = null

    override fun onServiceConnected(
        name: ComponentName?,
        service: IBinder?
    ) {
        Log.i(TAG, "onServiceConnected: name:${name?.packageName}")
        easyIpcService = IEasyIpcService.Stub.asInterface(service)
        try {
            easyIpcService?.asBinder()?.linkToDeath(this, 0)
            easyIpcService?.registerCallback(this)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.w(TAG, "onServiceDisconnected: name:${name?.packageName}")
    }

    override fun invoke(requestData: ByteArray?) : ByteArray? {
        return easyIpcService?.invoke(requestData)
    }

    override fun destroy() {
        if (easyIpcService?.asBinder()?.isBinderAlive == true) {
            try {
                easyIpcService?.unregisterCallback(this)
            } catch (e: RemoteException) {
                Log.e(TAG, "destroy:unregisterCallback:${e.message}")
            }
        }
    }

    override fun binderDied() {
        easyIpcService?.let {
            it.asBinder().unlinkToDeath(this, 0)
            easyIpcService = null
        }
    }

    override fun onCallback(data: ByteArray?) {
        data?.let {
            Log.i(TAG, "onCallback: data:${String(it)}")
        } ?: let {
            Log.i(TAG, "onCallback: data: null")
        }
    }

    companion object {
        private const val TAG = "EasyIpcServiceConnection"
    }
}