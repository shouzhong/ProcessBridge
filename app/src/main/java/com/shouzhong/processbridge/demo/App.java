package com.shouzhong.processbridge.demo;

import android.app.Application;

import com.shouzhong.processbridge.ProcessBridgeUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessBridgeUtils.init(null);
    }
}
