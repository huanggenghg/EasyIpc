package com.hghuangggeng.easyipc_core

import com.google.protobuf.ByteString
import com.hghuangggeng.easyipc_annotations.Constants
import com.hghuangggeng.easyipc_annotations.IIpcDataWrapper
import com.hghuangggeng.easyipc_annotations.IpcData
import com.huanggenghg.easyipc_transport_aidl.Ipc
import java.util.UUID
import kotlin.jvm.java
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberFunctions

object IpcCallUtils {
    fun buildRequest(
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
                val paramIpcDataWrapper = adaptToIpcDataWrapper(p)
                requestBuilder.addParameterTypes(paramIpcDataWrapper?.javaClass?.name)
                rawParamBytes = SerializationUtils.toBytes(paramIpcDataWrapper)
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

    fun buildErrorResponse(requestId: String, errorMessage: String): ByteArray {
        return Ipc.IpcResponse.newBuilder()
            .setRequestId(requestId)
            .setErrorMessage(errorMessage)
            .setIsError(true)
            .build().toByteArray()
    }

    fun buildSuccessResponse(
        requestId: String,
        result: Any
    ): ByteArray {
        val responseBuilder = Ipc.IpcResponse.newBuilder()
            .setRequestId(requestId)
            .setIsError(false)

        var rawResultBytes: ByteArray
        if (result.javaClass.isAnnotationPresent(IpcData::class.java)) {
            val resultIpcDataWrapper = adaptToIpcDataWrapper(result)
            responseBuilder.setResultType(resultIpcDataWrapper?.javaClass?.name)
            rawResultBytes = SerializationUtils.toBytes(resultIpcDataWrapper)
        } else {
            responseBuilder.setResultType(result.javaClass.name)
            rawResultBytes = SerializationUtils.toBytes(result)
        }
        return responseBuilder
            .setResultData(ByteString.copyFrom(rawResultBytes))
            .build().toByteArray()
    }

    fun convertResponse(resultData: ByteArray?): Any? {
        if (resultData == null) {
            return null
        }
        val response = Ipc.IpcResponse.parseFrom(resultData)
        if (response.isError) {
            return null
        }

        var originData: Any? = null
        SerializationUtils.fromBytesByClassName(
            response.resultData.toByteArray(),
            response.resultType
        )?.let {
            originData = if (it is IIpcDataWrapper<*>) {
                it.toOriginal()
            } else {
                it
            }
        }
        return originData
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