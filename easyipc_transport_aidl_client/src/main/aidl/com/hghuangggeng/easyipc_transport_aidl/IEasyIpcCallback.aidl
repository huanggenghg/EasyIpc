package com.hghuangggeng.easyipc_transport_aidl;

oneway interface IEasyIpcCallback {
    void onCallback(in byte[] data);
}