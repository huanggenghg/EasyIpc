package com.hghuangggeng.easyipc_annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class IpcMethod(val alias: String = "")
