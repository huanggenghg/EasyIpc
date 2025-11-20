package com.hghuangggeng.easyipc_core

import android.util.Log

object ReflectionUtil {
    private const val TAG = "ReflectionUtil"
    fun invokeMethod(className: String, methodName: String, args: List<Any>): Any {
        try {
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance()

            // 1. 获取所有声明的方法
            val methods = clazz.getDeclaredMethods()
            val argsTypes = args.map { it.javaClass }

            // 2. 手动查找兼容的方法
            val method = methods.find { m ->
                m.name == methodName && m.parameterTypes.size == argsTypes.size &&
                        m.parameterTypes.zip(argsTypes).all { (paramType, argType) ->
                            // 检查参数类型是否兼容 (paramType.isAssignableFrom(argType))
                            // 即 argType 是 paramType 的子类或实现
                            paramType.isAssignableFrom(argType)
                        }
            }
                ?: throw NoSuchMethodException("Cannot find a suitable method $methodName with compatible parameters in $className")

            // 3. 调用找到的方法
            method.isAccessible = true
            val invokeResult = method.invoke(instance, *args.toTypedArray())
            return invokeResult ?: true // 调用成功，invokeResult 为 null 是调用无返回值方法的返回
        } catch (e: Exception) {
            Log.e(TAG, "invokeMethod:${e.message}")
            throw e
        }
    }
}