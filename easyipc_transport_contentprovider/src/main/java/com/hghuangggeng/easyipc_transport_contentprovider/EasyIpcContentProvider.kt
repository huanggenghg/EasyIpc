package com.hghuangggeng.easyipc_transport_contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import com.hghuangggeng.easyipc_baseserver.BaseEasyIpcServer
import com.hghuangggeng.easyipc_baseserver.IEasyIpcServer
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback

class EasyIpcContentProvider : ContentProvider() {
    lateinit var easyIpcServer: IEasyIpcServer

    override fun call(authority: String, method: String, arg: String?, extras: Bundle?): Bundle? {
        val requestData = extras?.getByteArray("requestData")
        val clientMessengerBinder = extras?.getBinder("clientMessengerBinder")
        if (clientMessengerBinder == null) {
            // 为同步调用
            val resultData = easyIpcServer.onInvoke(requestData)
            val resultBundle = Bundle().apply {
                putByteArray("resultData", resultData)
            }
            return resultBundle
        } else {
            // 为异步回调调用
            easyIpcServer.onAsyncInvoke(requestData, object : IEasyIpcRawCallback {
                override fun onCallback(data: ByteArray?, requestId: String) {
                    val msg = Message.obtain().apply {
                        this.data.putString("requestId", requestId)
                        this.data.putByteArray("resultData", data)
                    }
                    val clientMessenger = Messenger(clientMessengerBinder)
                    clientMessenger.send(msg)
                }
            })
        }

        return null
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        easyIpcServer = BaseEasyIpcServer(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return 0
    }
}