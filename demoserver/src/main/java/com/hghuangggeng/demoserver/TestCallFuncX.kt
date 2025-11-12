package com.hghuangggeng.demoserver

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IpcMethod

class TestCallFuncX {
    @IpcMethod
    private fun helloX() {
        Log.i("TEST", "helloX")
    }
}