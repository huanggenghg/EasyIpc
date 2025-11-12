package com.hghuangggeng.easyipc_core

interface IEasyIpcServer {
    fun onInvoke(requestData: ByteArray?)
}