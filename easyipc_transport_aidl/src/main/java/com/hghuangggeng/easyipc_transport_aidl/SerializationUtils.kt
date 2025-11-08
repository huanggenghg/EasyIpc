package com.hghuangggeng.easyipc_transport_aidl

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

object SerializationUtils {
    // 使用 Moshi 实例作为单例，确保高性能
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * 将任意 Kotlin 对象序列化为 UTF-8 编码的 byte[] (JSON 格式)
     * 使用 Moshi 库保证高性能和易用性。
     *
     * @param data 要序列化的对象
     * @return 对象的 byte[] 表示
     */
    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T> toBytes(data: T?): ByteArray {
        val adapter = moshi.adapter<T>()
        val jsonString = adapter.toJson(data)
        return jsonString.toByteArray(StandardCharsets.UTF_8)
    }

    /**
     * 将 byte[] 反序列化回原始 Kotlin 对象
     *
     * @param bytes 对象的 byte[] 表示
     * @return 原始对象实例
     */
    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T : Any> fromBytes(bytes: ByteArray): T? {
        val jsonString = String(bytes, StandardCharsets.UTF_8)
        val adapter = moshi.adapter<T>()
        try {
            return adapter.fromJson(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 根据完整的类名字符串和字节数组进行动态反序列化。
     *
     * @param bytes JSON 数据的字节数组。
     * @param className 完整的类名，例如 "com.yourpackage.Person"。
     * @return 反序列化后的对象，需要手动转换为预期类型。
     */
    fun fromBytesByClassName(bytes: ByteArray, className: String): Any? {
        val jsonString = String(bytes, StandardCharsets.UTF_8)

        // 1. 使用反射获取 Class 对象
        val clazz: Class<*> = Class.forName(className)

        // 2. 获取 Moshi 适配器并反序列化
        // 注意：这里我们使用非泛型的 adapter() 方法，它接受 Class<?> 参数
        val adapter = moshi.adapter<Any>(clazz)

        return adapter.fromJson(jsonString)

    }

}
