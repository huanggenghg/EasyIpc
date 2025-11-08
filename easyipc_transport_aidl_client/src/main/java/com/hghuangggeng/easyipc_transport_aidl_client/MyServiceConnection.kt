package com.hghuangggeng.easyipc_transport_aidl_client

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Parcelable
import android.os.RemoteException
import android.util.Log
import com.hghuangggeng.easyipc_transport_aidl.CallParams
import com.hghuangggeng.easyipc_transport_aidl.CallResult
import com.hghuangggeng.easyipc_transport_aidl.IMsgManager
import com.hghuangggeng.easyipc_transport_aidl.IReceiveMsgListener
import com.hghuangggeng.easyipc_transport_aidl.IpcCallUtils
import com.hghuangggeng.easyipc_transport_aidl.Msg

class MyServiceConnection : ServiceConnection {

    private var deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            iMsgManager?.let {
                // 当 binder 连接断开时，解除注册
                it.asBinder().unlinkToDeath(this, 0)
                iMsgManager = null
            }
        }
    }

    private var receiveMsgListener = object : IReceiveMsgListener.Stub() {
        override fun onReceive(msg: Msg?) {
            Log.i(TAG, "onReceive: msg:${msg?.msg} time:${msg?.time}")
        }
    }
    private var iMsgManager: IMsgManager?  = null

    override fun onServiceConnected(
        name: ComponentName?,
        service: IBinder?
    ) {
        Log.i(TAG, "onServiceConnected: name:${name?.packageName}")
        iMsgManager = IMsgManager.Stub.asInterface(service)
        try {
            iMsgManager?.asBinder()?.linkToDeath(deathRecipient, 0)
            iMsgManager?.registerReceiveListener(receiveMsgListener)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.w(TAG, "onServiceDisconnected: name:${name?.packageName}")
    }

    fun sendMsg(msg: String) {
        iMsgManager?.sendMsg(Msg(msg, System.currentTimeMillis()))
    }

    fun call(callParams: CallParams<in Parcelable>) : CallResult<in Parcelable>? {
        return iMsgManager?.call(callParams)
    }

    fun invoke(param: Any) {
        val result = iMsgManager?.invoke(
            IpcCallUtils.prepareRpcCall(
                "call",
                params = listOf(param)
            )
        )
        result?.let {
            Log.i(TAG, "invoke:${String(it)}")
        } ?: apply {
            Log.i(TAG, "invoke:result null!")
        }
    }

    fun destroy() {
        if (iMsgManager?.asBinder()?.isBinderAlive == true) {
            try {
                iMsgManager?.unregisterReceiveListener(receiveMsgListener)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "MyServiceConnection"
    }
}