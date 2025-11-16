package com.hghuangggeng.easyipc_core

import android.util.Log
import com.hghuangggeng.easyipc_annotations.IIpcDataWrapper
import java.lang.reflect.Method

object ReflectionUtil {
    private const val TAG = "ReflectionUtil"
    fun invokeMethod(className: String, methodName: String, args: List<Any>): Any {
        try {
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance()

            val adaptArgs = mutableListOf<Any>()
            args.forEach {
                if (it is IIpcDataWrapper<*>) {
                    adaptArgs.add(it.toOriginal() as Any)
                } else {
                    adaptArgs.add(it)
                }
            }

            val parameterTypes = adaptArgs.map { it.javaClass }.toTypedArray()
            val method: Method = clazz.getDeclaredMethod(methodName, *parameterTypes)
            method.isAccessible = true

            val invokeResult = method.invoke(instance, *adaptArgs.toTypedArray())
            return invokeResult ?: true // 调用成功，invokeResult 为 null 是调用无返回值方法的返回
        } catch (e: Exception) { // 捕获异常，返回 false 调用失败
            Log.e(TAG, "invokeMethod:${e.message}")
            throw e
        }
    }
}