package com.hghuangggeng.easyipc_core

import android.content.Context
import android.util.Log
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
        // Step 1: 外部反序列化 RpcRequest 容器
        val request = Ipc.IpcRequest.parseFrom(requestData)
        val methodName = request.methodName
        val paramTypes = request.parameterTypesList
        val paramBytes = request.parametersList
        val params = mutableListOf<Any>()

        try {
            // Step 2: 参数类型识别与反序列化（核心反射步骤）
            for (i in paramTypes.indices) {
                val className = paramTypes[i]
                val bytes = paramBytes[i].toByteArray()
                // 使用内部序列化工具反序列化具体参数
                SerializationUtils.fromBytesByClassName(bytes, className)?.let {
                    params.add(it)
                }
            }

            // Step 3: 反射调用实际业务方法
            val methodRegistryClassName = methodRegistriesMap[methodName]
            if (methodRegistryClassName.isNullOrEmpty()) {
                Log.e(TAG, "onInvoke: service method not found!")
                return IpcCallUtils.buildErrorResponse(
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
            return IpcCallUtils.buildSuccessResponse(request.requestId, result)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "onInvoke: ${e.message}")
            return IpcCallUtils.buildErrorResponse(
                request.requestId,
                "Class not found: ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "onInvoke: ${e.message}")
            return IpcCallUtils.buildErrorResponse(
                request.requestId,
                "Deserialization failed: ${e.message}"
            )
        }
    }

    companion object {
        private const val TAG = "BaseEasyIpcServer"
    }
}