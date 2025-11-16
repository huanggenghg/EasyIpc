package com.hghuangggeng.easyipc_baseclient

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

    abstract fun invoke(requestData: ByteArray?): ByteArray?
}