package com.hghuangggeng.easyipc_transport_aidl;

import com.hghuangggeng.easyipc_transport_aidl.IEasyIpcCallback;

interface IEasyIpcService {
  	// 客户端注册监听回调
    void registerCallback(IEasyIpcCallback callback);
  	// 客户端取消监听回调
    void unregisterCallback(IEasyIpcCallback callback);
    // 通用调用
    byte[] invoke(in byte[] requestData);
}