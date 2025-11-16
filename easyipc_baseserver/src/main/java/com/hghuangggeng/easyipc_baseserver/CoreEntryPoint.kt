package com.hghuangggeng.easyipc_baseserver

import com.hghuangggeng.easyipc_annotations.IMethodRegistry
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CoreEntryPoint {
    fun methodRegistries(): Set<IMethodRegistry>
}