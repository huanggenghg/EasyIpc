package com.hghuangggeng.easyipc_baseserver

import android.content.Context
import android.util.Log
import com.hghuangggeng.easyipc_core.IpcCallUtils
import com.hghuangggeng.easyipc_core.ReflectionUtil
import com.huanggenghg.easyipc_transport_aidl.Ipc
import dagger.hilt.android.EntryPointAccessors

class BaseEasyIpcServer(context: Context) : IEasyIpcServer {
    private val methodRegistriesMap = mutableMapOf<String, String>()

    init {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            CoreEntryPoint::class.java
        )
        entryPoint.methodRegistries().forEach {
            it.register(methodRegistriesMap)
        }
    }

    override fun onInvoke(requestData: ByteArray?): ByteArray {
        return IpcCallUtils.invoke(requestData, methodRegistriesMap)
    }

    companion object {
        private const val TAG = "BaseEasyIpcServer"
    }
}