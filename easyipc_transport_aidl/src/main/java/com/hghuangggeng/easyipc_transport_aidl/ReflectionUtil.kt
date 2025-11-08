package com.hghuangggeng.easyipc_transport_aidl

object ReflectionUtil {
    fun invokeMethod(instance: Any, methodName: String, args: List<Any>): Any? {
        val clazz = instance.javaClass
        // 查找匹配方法（可能需要处理方法重载）
        val method = clazz.getDeclaredMethod(methodName, *args.map { it.javaClass }.toTypedArray())
        // 调用方法
        return method.invoke(instance, *args.toTypedArray())
    }
}