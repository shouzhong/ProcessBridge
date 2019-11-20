package com.shouzhong.processbridge;

import com.shouzhong.processbridge.activity.ActivityProcessBridge;
import com.shouzhong.processbridge.base.IProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridgeListener;
import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.eventbus.EventBusProcessBridge;
import com.shouzhong.processbridge.sp.SPProcessBridge;
import com.shouzhong.processbridge.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProcessBridgeUtils {

    public static void init(List<IProcessBridge> list) {
        final List<IProcessBridge> temps = new ArrayList<>();
        temps.add(EventBusProcessBridge.getDefault());
        temps.add(ActivityProcessBridge.getDefault());
        temps.add(SPProcessBridge.getDefault());
        if (list != null) temps.addAll(list);
        if (Utils.isMainProcess()) {
            ProcessBridge.init(Utils.getApp());
        } else {
            ProcessBridge.setProcessBridgeListener(new ProcessBridgeListener() {
                @Override
                public void onProcessBridgeConnected(Class<? extends ProcessBridgeService> service) {
                    if(service.getCanonicalName().equals(ProcessBridgeUtils.Service.class.getCanonicalName())) {
                        for (IProcessBridge bus : temps) {
                            bus.onProcessBridgeConnected(service);
                        }
                    }
                }

                @Override
                public void onProcessBridgeDisconnected(Class<? extends ProcessBridgeService> service) {
                    if(service.getCanonicalName().equals(ProcessBridgeUtils.Service.class.getCanonicalName())) {
                        for (IProcessBridge bus : temps) {
                            bus.onProcessBridgeDisconnected(service);
                        }
                    }
                }
            });
            ProcessBridge.connect(Utils.getApp(), ProcessBridgeUtils.Service.class);
        }
        for (IProcessBridge bus : temps) {
            bus.init();
        }
    }

    public static void destroy() {
        if (!Utils.isMainProcess()) {
            ProcessBridge.disconnect(Utils.getApp());
        }
    }

    public static EventBusProcessBridge getEventBus() {
        return EventBusProcessBridge.getDefault();
    }

    public static ActivityProcessBridge getActivityManager() {
        return ActivityProcessBridge.getDefault();
    }

    public static SPProcessBridge getSP() {
        return SPProcessBridge.getDefault();
    }

    public static class Service extends ProcessBridgeService { }

}
