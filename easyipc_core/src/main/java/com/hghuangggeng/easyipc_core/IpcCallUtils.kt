package com.hghuangggeng.easyipc_core

import android.util.Log
import com.google.protobuf.ByteString
import com.hghuangggeng.easyipc_annotations.Constants
import com.hghuangggeng.easyipc_annotations.IIpcDataWrapper
import com.hghuangggeng.easyipc_annotations.IpcData
import com.huanggenghg.easyipc_transport_aidl.Ipc
import java.util.UUID
import kotlin.jvm.java
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberFunctions
import kotlin.text.isNullOrEmpty

object IpcCallUtils {
    private const val TAG = "IpcCallUtils"
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

    fun invoke(requestData: ByteArray?, methodRegistriesMap: Map<String, String>): ByteArray {
        // Step 1: 外部反序列化 RpcRequest 容器
        val request = Ipc.IpcRequest.parseFrom(requestData)
        val methodName = request.methodName
        val paramTypes = request.parameterTypesList
        val paramBytes = request.parametersList

        try {
            // Step 2: 参数类型识别与反序列化（核心反射步骤）
            val params = convertParams(paramTypes, paramBytes)
            // Step 3: 反射调用实际业务方法
            val methodRegistryClassName = methodRegistriesMap[methodName]
            if (methodRegistryClassName.isNullOrEmpty()) {
                Log.e(TAG, "onInvoke: service method not found!")
                return buildErrorResponse(
                    request.requestId,
                    "Service method not found"
                )
            }

            val result = ReflectionUtil.invokeMethod(
                methodRegistryClassName,
                methodName,
                params
            )
            // Step 4: 封装响应并返回
            return buildSuccessResponse(request.requestId, result)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "onInvoke: ${e.message}")
            return buildErrorResponse(
                request.requestId,
                "Class not found: ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "onInvoke: ${e.message}")
            return buildErrorResponse(
                request.requestId,
                "Deserialization failed: ${e.message}"
            )
        }
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

    fun convertParams(paramTypes: List<String>, paramBytes: List<ByteString>): List<Any> {
        val params = mutableListOf<Any>()
        // 参数类型识别与反序列化（核心反射步骤）
        for (i in paramTypes.indices) {
            val className = paramTypes[i]
            val bytes = paramBytes[i].toByteArray()
            // 使用内部序列化工具反序列化具体参数
            SerializationUtils.fromBytesByClassName(bytes, className)?.let {
                params.add(it)
            }
        }

        val adaptArgs = mutableListOf<Any>()
        params.forEach {
            if (it is IIpcDataWrapper<*>) {
                adaptArgs.add(it.toOriginal() as Any)
            } else {
                adaptArgs.add(it)
            }
        }
        return adaptArgs
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