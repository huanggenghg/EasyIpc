package com.hghuangggeng.easyipc_annotations

interface IIpcDataWrapper<T> {
    fun toOriginal(): T
}