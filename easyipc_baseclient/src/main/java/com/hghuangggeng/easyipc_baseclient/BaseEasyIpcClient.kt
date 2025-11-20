package com.hghuangggeng.easyipc_baseclient

import com.hghuangggeng.easyipc_core.IEasyIpcDataCallback
import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback
import com.hghuangggeng.easyipc_core.IpcCallUtils

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
        asyncInvoke(
            IpcCallUtils.buildRequest(
                funcName,
                true,
                param.toList()
            ), object : IEasyIpcRawCallback {
                override fun onCallback(data: ByteArray?) {
                    callback?.onCallback(IpcCallUtils.convertResponse(data))
                }
            }
        )
    }

    abstract fun invoke(requestData: ByteArray?): ByteArray?

    abstract fun asyncInvoke(requestData: ByteArray?, callback: IEasyIpcRawCallback?)
}