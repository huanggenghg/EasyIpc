package com.hghuangggeng.democlient

import com.hghuangggeng.easyipc_annotations.IpcData

@IpcData
data class TestCallParams2(val value: Int, val data: String)