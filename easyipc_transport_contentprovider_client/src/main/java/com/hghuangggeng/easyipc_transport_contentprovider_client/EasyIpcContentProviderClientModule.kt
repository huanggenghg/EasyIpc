package com.hghuangggeng.easyipc_transport_contentprovider_client

import com.hghuangggeng.easyipc_baseclient.IEasyIpcClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EasyIpcContentProviderClientModule {

    @Singleton
    @Binds
    abstract fun bindEasyIpcClient(
        easyIpcContentProviderClient: EasyIpcContentProviderClient
    ): IEasyIpcClient
}