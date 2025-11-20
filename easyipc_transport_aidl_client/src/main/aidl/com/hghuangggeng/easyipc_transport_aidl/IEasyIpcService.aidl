package com.hghuangggeng.easyipc_transport_aidl;

import com.hghuangggeng.easyipc_transport_aidl.IEasyIpcCallback;

interface IEasyIpcService {
    // 通用调用
    byte[] invoke(in byte[] requestData);
    // 异步调用
    void asyncInvoke(in byte[] requestData, IEasyIpcCallback callback);
}