package com.hghuangggeng.easyipc_baseserver

import com.hghuangggeng.easyipc_core.IEasyIpcRawCallback

interface IEasyIpcServer {
    fun onInvoke(requestData: ByteArray?) : ByteArray
    fun onAsyncInvoke(requestData: ByteArray?, callback: IEasyIpcRawCallback)
}