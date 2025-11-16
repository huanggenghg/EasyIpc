package com.hghuangggeng.demoserver

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IpcMethod

class TestCallFunc2 {
    @IpcMethod
    private fun hello2() {
        Log.i("TEST", "hello")
    }

    @IpcMethod
    private fun call(params2: TestCallParams2) : TestCallResult {
        Log.i("TEST", params2.data)
        return TestCallResult("${params2.data}_ResultForClient")
    }
}