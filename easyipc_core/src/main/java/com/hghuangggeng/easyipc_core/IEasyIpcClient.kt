package com.hghuangggeng.easyipc_core

import android.content.Context
import androidx.lifecycle.Lifecycle

internal interface IEasyIpcClient {
    fun start(context: Context, lifecycle: Lifecycle?)
    fun invoke(funcName: String, vararg param: Any)
}