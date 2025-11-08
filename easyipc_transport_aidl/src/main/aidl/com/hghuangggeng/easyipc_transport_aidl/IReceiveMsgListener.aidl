// IReceiveMsgListener.aidl
package com.hghuangggeng.easyipc_transport_aidl;

import com.hghuangggeng.easyipc_transport_aidl.Msg;

interface IReceiveMsgListener {
    void onReceive(in Msg msg);
}