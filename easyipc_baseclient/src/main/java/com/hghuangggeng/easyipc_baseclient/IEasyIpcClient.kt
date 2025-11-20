package com.hghuangggeng.easyipc_baseclient

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.hghuangggeng.easyipc_core.IEasyIpcDataCallback

interface IEasyIpcClient {
    fun start(context: Context, serverPackage: String, lifecycle: Lifecycle? = null)
    fun invoke(funcName: String, vararg param: Any) : Any?
    fun asyncInvoke(funcName: String, vararg param: Any, callback : IEasyIpcDataCallback?)
}