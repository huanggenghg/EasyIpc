package com.hghuangggeng.easyipc_transport_aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hghuangggeng.easyipc_annotations.IMethodRegistry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EasyIpcService : Service() {
    @Inject
    lateinit var registries: Set<@JvmSuppressWildcards IMethodRegistry>

    override fun onBind(intent: Intent): IBinder {
        return EasyIpcBinder(registries)
    }
}
