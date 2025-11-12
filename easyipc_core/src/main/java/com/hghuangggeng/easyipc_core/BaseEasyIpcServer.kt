package com.hghuangggeng.easyipc_core

import com.huanggenghg.easyipc_transport_aidl.Ipc

class BaseEasyIpcServer : IEasyIpcServer {


    override fun onInvoke(requestData: ByteArray?) {
        // Step 1: 外部反序列化 RpcRequest 容器
        val request = Ipc.IpcRequest.parseFrom(requestData)

        val methodName = request.methodName
        val paramTypes = request.parameterTypesList
        val paramBytes = request.parametersList

        val actualParams = mutableListOf<Any>()

        // Step 2: 参数类型识别与反序列化（核心反射步骤）
        for (i in paramTypes.indices) {
            val className = paramTypes[i]
            val bytes = paramBytes[i].toByteArray()
            try {
                // B. 使用内部序列化工具反序列化具体参数
                val paramInstance = SerializationUtils.fromBytesByClassName(
                    bytes,
                    className
                ) // todo 限制同意包下类，需扩展任意包下// 在转化为原始类进行调用
                paramInstance?.let {
                    actualParams.add(it)
                }

            } catch (e: ClassNotFoundException) {
                // 处理约定类不存在的错误
//                    return buildErrorResponse(request.requestId, "Class not found: $className").toByteArray()
//                return null
            } catch (e: Exception) {
                // 处理反序列化错误
//                    return buildErrorResponse(request.requestId, "Deserialization failed: ${e.message}").toByteArray()
//                return null
            }
        }


        // Step 3: 反射调用实际业务方法
//            val serviceInstance = getServiceByName(request.serviceName) // 查找实际的服务实现对象
//            val result = ReflectionUtil.invokeMethod(serviceInstance, methodName, actualParams)
//
//            // Step 4: 封装响应并返回
//            val response = buildSuccessResponse(request.requestId, result)
//        return Ipc.IpcResponse.newBuilder().setResultType("ok").build().toByteArray()
    }

    companion object {
        private const val TAG = "BaseEasyIpcServer"
    }
}