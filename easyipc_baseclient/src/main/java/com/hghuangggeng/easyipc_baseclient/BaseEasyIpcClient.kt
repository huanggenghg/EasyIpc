package com.hghuangggeng.easyipc_baseclient

import com.hghuangggeng.easyipc_core.IEasyIpcDataCallback
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import com.hghuangggeng.easyipc_core.IpcCallUtils
import java.util.UUID

abstract class BaseEasyIpcClient : IEasyIpcClient {
    override fun invoke(funcName: String, vararg param: Any): Any? {
        val resultData = invoke(
            IpcCallUtils.buildRequest(
                funcName,
                params = param.toList()
            )
        )

        return IpcCallUtils.convertResponse(resultData)
    }

    override fun asyncInvoke(funcName: String, vararg param: Any, callback: IEasyIpcDataCallback?) {
        val requestId =
            UUID.randomUUID().toString() // 异步回调需要维护一个 requestId, 提供给 contentProvider client 使用
        asyncInvoke(
            IpcCallUtils.buildRequest(
                funcName,
                requestId,
                true,
                param.toList()
            ), requestId, object : IEasyIpcRawCallback {
                override fun onCallback(data: ByteArray?, requestId: String) {
                    callback?.onCallback(IpcCallUtils.convertResponse(data))
                }
            }
        )
    }

    abstract fun invoke(requestData: ByteArray?): ByteArray?

    abstract fun asyncInvoke(
        requestData: ByteArray?,
        requestId: String,
        callback: IEasyIpcRawCallback?
    )
}