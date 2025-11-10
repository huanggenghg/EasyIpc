package com.hghuangggeng.easyipc_transport_aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log
import com.hghuangggeng.easyipc_annotations.IMethodRegistry
import com.huanggenghg.easyipc_transport_aidl.Ipc
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MyService : Service() {

    private val receiveListeners = RemoteCallbackList<IReceiveMsgListener>()

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    @Inject
    lateinit var ipcManager: IpcMethodManager

    @Inject
    lateinit var registries: Set<@JvmSuppressWildcards IMethodRegistry>

    inner class MyBinder: IMsgManager.Stub() {
        override fun sendMsg(msg: Msg?) {
            // server process request at here
            val n = receiveListeners.beginBroadcast()
            for (i in 0 until n) {
                val listener = receiveListeners.getBroadcastItem(i)
                listener?.let {
                    try {
                        val serverMsg = Msg("服务器响应 ${Date(System.currentTimeMillis())}\n $packageName", System.currentTimeMillis())
                        listener.onReceive(serverMsg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
            receiveListeners.finishBroadcast()
        }

        override fun registerReceiveListener(listener: IReceiveMsgListener?) {
            receiveListeners.register(listener)
        }

        override fun unregisterReceiveListener(listener: IReceiveMsgListener?) {
            val success = receiveListeners.unregister(listener)
            if (success) {
                Log.d(TAG, "解除注册成功")
            } else {
                Log.d(TAG, "解除注册失败")
            }
        }

        override fun call(callParams: CallParams<in Parcelable>): CallResult<in Parcelable>? {
            return CallResult(Msg("服务器call响应 ${Date(System.currentTimeMillis())}\n $packageName", System.currentTimeMillis()),true)
        }

        override fun invoke(requestData: ByteArray?): ByteArray? {
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
                    val paramInstance = SerializationUtils.fromBytesByClassName(bytes, className) // todo 限制同意包下类，需扩展任意包下// 在转化为原始类进行调用
                    paramInstance?.let {
                        actualParams.add(it)
                    }

                } catch (e: ClassNotFoundException) {
                    // 处理约定类不存在的错误
//                    return buildErrorResponse(request.requestId, "Class not found: $className").toByteArray()
                    return null
                } catch (e: Exception) {
                    // 处理反序列化错误
//                    return buildErrorResponse(request.requestId, "Deserialization failed: ${e.message}").toByteArray()
                    return null
                }
            }

            val map = mutableMapOf<String, String>()
            registries.forEach { // todo 分发
                it.register(map)
            }
            map.forEach {
                Log.i(TAG, "registerMap: ${it.key} ${it.value}")
            }


            // Step 3: 反射调用实际业务方法
//            val serviceInstance = getServiceByName(request.serviceName) // 查找实际的服务实现对象
//            val result = ReflectionUtil.invokeMethod(serviceInstance, methodName, actualParams)
//
//            // Step 4: 封装响应并返回
//            val response = buildSuccessResponse(request.requestId, result)
             return Ipc.IpcResponse.newBuilder().setResultType("ok").build().toByteArray()
        }
    }

    companion object {
        private const val TAG = "MyService"
    }
}
