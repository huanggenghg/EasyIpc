package com.hghuangggeng.easyipc_transport_aidl

import com.google.protobuf.ByteString
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
                val adaptP = adaptToProtocol(p)
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

    /**
     * 通用映射函数：使用反射动态调用替代类的 fromOriginal 伴生方法。
     */
    private fun adaptToProtocol(original: Any): Any? {
        val originalClass = original.javaClass
        val protocolPackage = "com.hghghgh.text"

        // 动态构建替代类的全限定名 (FQCN)
        val protocolClassName = "$protocolPackage.${originalClass.simpleName}Protocol"

        val protocolCompanionObject = Class.forName(protocolClassName).kotlin.companionObject

        return protocolCompanionObject?.memberFunctions?.find { func ->
            func.name == "fromOriginal"
        }?.call(protocolCompanionObject.objectInstance, original)
    }
}