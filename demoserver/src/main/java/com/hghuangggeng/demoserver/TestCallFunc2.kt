package com.hghuangggeng.demoserver

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IMethodRegistry
import com.hghuangggeng.easyipc_annotations.IpcMethod

class TestCallFunc2 {
    @IpcMethod
    private fun hello2() {
        Log.i("TEST", "hello")
    }
}