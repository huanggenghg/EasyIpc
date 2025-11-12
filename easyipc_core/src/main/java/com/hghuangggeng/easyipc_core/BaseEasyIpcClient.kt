package com.hghuangggeng.easyipc_core

abstract class BaseEasyIpcClient : IEasyIpcClient {
    override fun invoke(funcName: String, vararg param: Any) {
        val resultData = invoke(
            IpcCallUtils.prepareRpcCall(
                funcName,
                params = param.toList()
            )
        )
        // to convert to object
    }

    abstract fun invoke(requestData: ByteArray?): ByteArray?
}