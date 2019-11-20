package com.shouzhong.processbridge.base;

public abstract class ProcessBridgeListener {

    public abstract void onProcessBridgeConnected(Class<? extends ProcessBridgeService> service);

    public void onProcessBridgeDisconnected(Class<? extends ProcessBridgeService> service) {

    }

}
