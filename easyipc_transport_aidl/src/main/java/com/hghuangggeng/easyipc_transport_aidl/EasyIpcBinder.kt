package com.hghuangggeng.easyipc_transport_aidl

import android.content.Context
import android.os.RemoteCallbackList
import android.util.Log
import com.hghuangggeng.easyipc_annotations.IMethodRegistry
import com.hghuangggeng.easyipc_core.BaseEasyIpcServer
import com.hghuangggeng.easyipc_core.IEasyIpcServer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlin.jvm.java

class EasyIpcBinder(private val context: Context) : IEasyIpcService.Stub() {

    private val remoteCallbackList = RemoteCallbackList<IEasyIpcCallback>()
    private val easyIpcServer: IEasyIpcServer = BaseEasyIpcServer() // todo di


    init {

//        val map = mutableMapOf<String, String>()
//        hiltEntryPoin
//        hiltEntryPoint.().register(map)
//        map.forEach {
//            Log.i(TAG, "map:${it.key}:${it.value}")
//        }
    }

    override fun registerCallback(callback: IEasyIpcCallback?) {
        remoteCallbackList.register(callback).also {
            Log.i(TAG, "registerCallback:$it")
        }
    }

    override fun unregisterCallback(callback: IEasyIpcCallback?) {
        remoteCallbackList.unregister(callback).also {
            Log.i(TAG, "unregisterCallback:$it")

        }
    }

    override fun invoke(requestData: ByteArray?): ByteArray? {
        // todo 分发
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            CoreEntryPoint::class.java
        )

        // 手动获取集合
        val map = mutableMapOf<String, String>()
        entryPoint.methodRegistries().forEach {
            it.register(map)
        }
        map.forEach {
            Log.i(TAG, "${it.key}:${it.value}")
        }
        easyIpcServer.onInvoke(requestData)
        return null
    }

    companion object {
        private const val TAG = "EasyIpcBinder"
    }
}