package com.hghuangggeng.easyipc_core

import com.google.protobuf.ByteString
import com.hghuangggeng.easyipc_annotations.Constants
import com.hghuangggeng.easyipc_annotations.IpcData
import com.huanggenghg.easyipc_transport_aidl.Ipc
import java.util.UUID
import kotlin.jvm.java
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberFunctions

object IpcCallUtils {
    fun prepareRpcCall(
        method: String,
        isAsync: Boolean = false,
        params: List<Any> = emptyList()
    ): ByteArray? {
        val requestBuilder = Ipc.IpcRequest.newBuilder()
            .setRequestId(UUID.randomUUID().toString())
            .setMethodName(method)
            .setIsAsync(isAsync)
        for (p in params) {
            // Step 2: 内部参数序列化 (假设使用 JSON)
            var rawParamBytes: ByteArray
            if (p.javaClass.isAnnotationPresent(IpcData::class.java)) {
                val adaptP = adaptToIpcDataWrapper(p)
                requestBuilder.addParameterTypes(adaptP?.javaClass?.name)
                rawParamBytes = SerializationUtils.toBytes(adaptP)
            } else {
                requestBuilder.addParameterTypes(p.javaClass.name)
                rawParamBytes = SerializationUtils.toBytes(p)
            }
            requestBuilder.addParameters(ByteString.copyFrom(rawParamBytes))
        }
        val request = requestBuilder.build()
        // Step 4: 外部序列化为原始 byte[]
        return request.toByteArray()
    }

    private fun adaptToIpcDataWrapper(original: Any): Any? {
        val originalClass = original.javaClass
        val ipcDataWrapperClassName =
            "${Constants.GENERATED_PACKAGE_NAME}.${originalClass.simpleName}_${Constants.GENERATED_IPC_DATA_WRAPPER_SUFFIX}"
        val ipcDataWrapperObject = Class.forName(ipcDataWrapperClassName).kotlin.companionObject
        return ipcDataWrapperObject?.memberFunctions?.find { func ->
            func.name == Constants.GENERATED_IPC_DATA_WRAPPER_FUNC_FROM_ORIGINAL
        }?.call(ipcDataWrapperObject.objectInstance, original)
    }
}