package com.hghuangggeng.demoserver

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IpcMethod

class TestCallFunc3 {
    @IpcMethod
    private fun hello3() {
        Log.i("TEST", "hello")
    }
}