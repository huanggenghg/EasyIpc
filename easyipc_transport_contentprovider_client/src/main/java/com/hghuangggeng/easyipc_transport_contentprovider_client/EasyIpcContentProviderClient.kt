package com.hghuangggeng.easyipc_transport_contentprovider_client

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.hghuangggeng.easyipc_baseclient.BaseEasyIpcClient
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import javax.inject.Inject
import androidx.core.net.toUri

class EasyIpcContentProviderClient @Inject constructor() : BaseEasyIpcClient() {
    private lateinit var resolver: ContentResolver
    private lateinit var uri: Uri

    override fun invoke(requestData: ByteArray?): ByteArray? {
        val extras = Bundle().apply {
            putByteArray("requestData", requestData)
        }

        val result = resolver.call(uri, "", null, extras)
        return result?.getByteArray("resultData")
    }

    override fun asyncInvoke(
        requestData: ByteArray?,
        callback: IEasyIpcRawCallback?
    ) {
        //
    }

    override fun start(context: Context, serverPackage: String, lifecycle: Lifecycle?) {
        resolver = context.contentResolver
        uri = "content://${serverPackage}.EasyIpcContentProvider".toUri()
    }
}