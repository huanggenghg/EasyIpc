package com.hghuangggeng.easyipc_baseserver

import android.content.Context
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import com.hghuangggeng.easyipc_core.IpcCallUtils
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

    override fun onAsyncInvoke(
        requestData: ByteArray?,
        callback: IEasyIpcRawCallback
    ) {
        IpcCallUtils.asyncInvoke(requestData, methodRegistriesMap, callback)
    }
}