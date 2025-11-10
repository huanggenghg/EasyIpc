package com.hghuangggeng.demoserver

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IpcMethod

class TestCallFunc {
    @IpcMethod
    private fun hello() {
        Log.i("TEST", "hello")
    }
}