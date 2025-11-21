package com.hghuangggeng.easyipc_core

interface IEasyIpcRawCallback {
    fun onCallback(data: ByteArray?, requestId: String)
}