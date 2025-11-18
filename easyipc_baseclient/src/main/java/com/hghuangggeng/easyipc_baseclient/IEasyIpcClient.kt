package com.hghuangggeng.easyipc_baseclient

import android.content.Context
import androidx.lifecycle.Lifecycle

interface IEasyIpcClient {
    fun start(context: Context, serverPackage: String, lifecycle: Lifecycle? = null)
    fun invoke(funcName: String, vararg param: Any) : Any?
}