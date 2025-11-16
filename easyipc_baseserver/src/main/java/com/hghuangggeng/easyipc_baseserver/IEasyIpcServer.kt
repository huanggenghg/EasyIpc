package com.hghuangggeng.easyipc_baseserver

interface IEasyIpcServer {
    fun onInvoke(requestData: ByteArray?) : ByteArray
}