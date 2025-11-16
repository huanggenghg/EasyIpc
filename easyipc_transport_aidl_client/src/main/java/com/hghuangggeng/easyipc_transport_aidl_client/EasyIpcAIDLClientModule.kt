package com.hghuangggeng.easyipc_transport_aidl_client

import com.hghuangggeng.easyipc_baseclient.IEasyIpcClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EasyIpcAIDLClientModule {

    @Singleton
    @Binds
    abstract fun bindAnalyticsService(
        analyticsServiceImpl: EasyIpcAIDLClient
    ): IEasyIpcClient
}