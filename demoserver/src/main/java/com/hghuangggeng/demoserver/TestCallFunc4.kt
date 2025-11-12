package com.hghuangggeng.demoserver

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IpcMethod

class TestCallFunc4 {
    @IpcMethod
    private fun hello5() {
        Log.i("TEST", "hello4")
    }
}