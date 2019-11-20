package com.shouzhong.processbridge.eventbus;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.shouzhong.processbridge.base.BaseProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.concurrentutils.util.Action;
import com.shouzhong.processbridge.concurrentutils.util.Function;
import com.shouzhong.processbridge.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventBusProcessBridge extends BaseProcessBridge<IMainService> {

    private static volatile EventBusProcessBridge sInstance = null;

    private EventBusProcessBridge() {
        super();
    }

    public static EventBusProcessBridge getDefault() {
        if (sInstance == null) {
            synchronized (EventBusProcessBridge.class) {
                if (sInstance == null) {
                    sInstance = new EventBusProcessBridge();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void init() {
        super.init();
        if (mMainProcess) {
            ProcessBridge.register(MainService.class);
            api = MainService.getInstance();
        } else {
            ProcessBridge.register(SubService.class);
        }
        Utils.getApp().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

            @Override
            public void onActivityStarted(Activity activity) { }

            @Override
            public void onActivityResumed(Activity activity) { }

            @Override
            public void onActivityPaused(Activity activity) { }

            @Override
            public void onActivityStopped(Activity activity) { }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (isRegistered(activity)) unregister(activity);
            }
        });
    }

    @Override
    public void onProcessBridgeConnected(Class<? extends ProcessBridgeService> service) {
        IMainService mainService = ProcessBridge.getInstanceInService(service, IMainService.class);
        ISubService subService = SubService.getInstance();
        mainService.register(pid, subService);
        mRemoteApis.set(mainService);
        mState = STATE_CONNECTED;
        List<Object> list = mainService.getStickyEvent();
        if (list != null) {
            for (Object o : list) {
                subService.postSticky(o);
            }
        }
    }

    @Override
    public void onProcessBridgeDisconnected(Class<? extends ProcessBridgeService> service) {
        mState = STATE_DISCONNECTED;
        mRemoteApis.action(new Action<IMainService>() {
            @Override
            public void call(IMainService o) {
                o.unregister(pid);
            }
        });
    }

    public void register(Object subscriber) {
        if (subscriber == null) return;
        EventBus.getDefault().register(subscriber);
        try {
            Method[] methods = subscriber.getClass().getDeclaredMethods();
            if (methods == null || methods.length == 0) return;
            final Set<Class> set = new HashSet<>();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(Subscribe.class)) continue;
                Class<?>[] parameterTypes = method.getParameterTypes();
                set.add(parameterTypes[0]);
            }
            if (set.size() == 0) return;
            final int id = Utils.hashCode(subscriber);
            actionInternal(new Action<IMainService>() {
                @Override
                public void call(IMainService o) {
                    for (Class cls : set) {
                        o.putEventType(id, cls.getName());
                    }
                }
            });
        } catch (Exception e) {}
    }

    public boolean isRegistered(Object subscriber) {
        if (subscriber == null) return false;
        return EventBus.getDefault().isRegistered(subscriber);
    }

    public void unregister(Object subscriber) {
        if (subscriber == null) return;
        EventBus.getDefault().unregister(subscriber);
        try {
            final int id = Utils.hashCode(subscriber);
            actionInternal(new Action<IMainService>() {
                @Override
                public void call(IMainService o) {
                    o.removeEventType(id);
                }
            });
        } catch (Exception e) {}
    }

    public void post(final Object event) {
        if (event == null) return;
        actionInternal(new Action<IMainService>() {
            @Override
            public void call(IMainService o) {
                o.post(event);
            }
        });
    }

    public void cancelEventDelivery(final Object event){
        if (event == null) return;
        actionInternal(new Action<IMainService>() {
            @Override
            public void call(IMainService o) {
                o.cancelEventDelivery(event);
            }
        });
    }

    public void postSticky(final Object event) {
        if (event == null) return;
        actionInternal(new Action<IMainService>() {
            @Override
            public void call(IMainService o) {
                o.postSticky(event);
            }
        });
    }

    /**
     * 获取某个粘滞事件
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> T getStickyEvent(final Class<T> eventType) {
        if (eventType == null) return null;
        try {
            return calculateInternal(new Function<IMainService, T>() {
                @Override
                public T call(IMainService o) {
                    return eventType.cast(o.getStickyEvent(eventType.getName()));
                }
            });
        } catch (Exception e) {}
        return null;
    }

    /**
     * 移除某个粘滞事件
     *
     * @param eventType
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T removeStickyEvent(final Class<T> eventType) throws Exception {
        if (eventType == null) return null;
        return calculateInternal(new Function<IMainService, T>() {
            @Override
            public T call(IMainService o) {
                return eventType.cast(o.removeStickyEvent(eventType.getName()));
            }
        });
    }

    /**
     * 移除某个粘滞事件
     *
     * @param event
     * @return
     * @throws Exception
     */
    public boolean removeStickyEvent(final Object event) throws Exception {
        if (event == null) return false;
        return calculateInternal(new Function<IMainService, Boolean>() {
            @Override
            public Boolean call(IMainService o) {
                return o.removeStickyEvent(event);
            }
        });
    }

    /**
     * 移除所有粘滞事件
     *
     */
    public void removeAllStickyEvents() {
        actionInternal(new Action<IMainService>() {
            @Override
            public void call(IMainService o) {
                o.removeAllStickyEvents();
            }
        });
    }

    /**
     * 是否订阅某个类型的事件，注意，不支持泛型
     *
     * @param cls
     * @return
     * @throws Exception
     */
    public boolean hasSubscriberForEvent(final Class cls) throws Exception {
        return calculateInternal(new Function<IMainService, Boolean>() {
            @Override
            public Boolean call(IMainService o) {
                return o.hasSubscriberForEvent(cls.getName());
            }
        });
    }

}
