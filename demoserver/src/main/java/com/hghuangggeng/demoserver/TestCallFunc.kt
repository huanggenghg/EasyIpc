package com.hghuangggeng.demoserver

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.hghuangggeng.easyipc_annotations.IpcMethod
import com.hghuangggeng.easyipc_core.IEasyIpcDataCallback

class TestCallFunc {
    @IpcMethod
    fun hello1(callback: IEasyIpcDataCallback) {
        Log.i("TEST", "helloCallback")
        Handler(Looper.getMainLooper()).postDelayed({
            callback.onCallback("hello world")
        }, 5000)
    }
}