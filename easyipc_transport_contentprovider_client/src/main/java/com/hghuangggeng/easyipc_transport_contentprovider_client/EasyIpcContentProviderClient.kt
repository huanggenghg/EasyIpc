package com.hghuangggeng.easyipc_transport_contentprovider_client

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.hghuangggeng.easyipc_baseclient.BaseEasyIpcClient
import javax.inject.Inject

class EasyIpcContentProviderClient @Inject constructor() : BaseEasyIpcClient() {
    private lateinit var resolver: ContentResolver
    private val uri = Uri.parse("content://com.hghuangggeng.easyipc_transport_contentprovider.EasyIpcContentProvider")

    override fun invoke(requestData: ByteArray?): ByteArray? {
        val extras = Bundle().apply {
            putByteArray("requestData", requestData)
        }

        val result = resolver.call(uri, "", null, extras)
        return result?.getByteArray("resultData")
    }

    override fun start(context: Context, lifecycle: Lifecycle?) {
        resolver = context.contentResolver
    }
}