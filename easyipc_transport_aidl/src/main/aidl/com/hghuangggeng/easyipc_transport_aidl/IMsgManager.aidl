// IMsgManager.aidl
package com.hghuangggeng.easyipc_transport_aidl;

// Declare any non-default types here with import statements

import com.hghuangggeng.easyipc_transport_aidl.Msg;
import com.hghuangggeng.easyipc_transport_aidl.CallResult;
import com.hghuangggeng.easyipc_transport_aidl.CallParams;
import com.hghuangggeng.easyipc_transport_aidl.IReceiveMsgListener;

interface IMsgManager {
  	// 发消息
    void sendMsg(in Msg msg);
  	// 客户端注册监听回调
    void registerReceiveListener(IReceiveMsgListener listener);
  	// 客户端取消监听回调
    void unregisterReceiveListener(IReceiveMsgListener listener);

    CallResult call(in CallParams params);

    // 通用调用
    byte[] invoke(in byte[] requestData);
}