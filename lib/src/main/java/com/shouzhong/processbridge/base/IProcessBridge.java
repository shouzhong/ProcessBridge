package com.shouzhong.processbridge.base;

public interface IProcessBridge {

    void init();

    void onProcessBridgeConnected(Class<? extends ProcessBridgeService> service);

    void onProcessBridgeDisconnected(Class<? extends ProcessBridgeService> service);

}
