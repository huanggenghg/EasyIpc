package com.hghuangggeng.easyipc_transport_aidl

import com.hghuangggeng.easyipc_annotations.IMethodRegistry
import javax.inject.Inject
import javax.inject.Singleton

// 在核心模块中使用所有注册的函数
@Singleton
class IpcMethodManager @Inject constructor(
    private val registries: Set<@JvmSuppressWildcards IMethodRegistry> // Hilt 自动注入包含所有实现类的 Set
) {
    fun initialize() {
        val map = mutableMapOf<String, String>()
        for (registry in registries) {
            registry.register(map)
        }
        // 现在 map 包含了所有业务模块注册的函数
    }

    fun getSet() = registries
}