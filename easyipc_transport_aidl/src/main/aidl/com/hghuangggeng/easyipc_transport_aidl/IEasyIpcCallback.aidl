package com.hghuangggeng.easyipc_transport_aidl;

interface IEasyIpcCallback {
    void onCallback(in byte[] data);
}