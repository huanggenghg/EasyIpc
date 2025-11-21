package com.hghuangggeng.easyipc_transport_contentprovider_client

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Messenger
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.hghuangggeng.easyipc_baseclient.BaseEasyIpcClient
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import javax.inject.Inject


class EasyIpcContentProviderClient @Inject constructor() : BaseEasyIpcClient(),
    DefaultLifecycleObserver {
    private lateinit var resolver: ContentResolver
    private lateinit var uri: Uri
    private lateinit var asyncCallbackHandler: EasyIpcAsyncCallbackHandler
    private lateinit var clientMessenger: Messenger

    override fun invoke(requestData: ByteArray?): ByteArray? {
        val extras = Bundle().apply {
            putByteArray("requestData", requestData)
        }

        val result = resolver.call(uri, "", null, extras)
        return result?.getByteArray("resultData")
    }

    override fun asyncInvoke(
        requestData: ByteArray?,
        requestId: String,
        callback: IEasyIpcRawCallback?
    ) {
        asyncCallbackHandler.registerCallback(requestId, callback)

        val extras = Bundle().apply {
            putByteArray("requestData", requestData)
            putBinder("clientMessengerBinder", clientMessenger.binder)
        }
        resolver.call(uri, "", null, extras)
    }

    override fun start(context: Context, serverPackage: String, lifecycle: Lifecycle?) {
        resolver = context.contentResolver
        uri = "content://${serverPackage}.EasyIpcContentProvider".toUri()
        asyncCallbackHandler = EasyIpcAsyncCallbackHandler()
        clientMessenger = Messenger(asyncCallbackHandler)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        asyncCallbackHandler.removeAllCallback()
    }
}