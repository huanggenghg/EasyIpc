package com.hghuangggeng.demoserver

import com.hghuangggeng.easyipc_annotations.IpcData

@IpcData
data class TestCallParams2(val value: Int, val data: String)