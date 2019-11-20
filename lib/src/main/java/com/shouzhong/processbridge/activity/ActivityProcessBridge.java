package com.shouzhong.processbridge.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.shouzhong.processbridge.base.BaseProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.concurrentutils.util.Action;
import com.shouzhong.processbridge.concurrentutils.util.Function;
import com.shouzhong.processbridge.util.Utils;

public class ActivityProcessBridge extends BaseProcessBridge<IActivityService> {
    private static volatile ActivityProcessBridge instance;

    private ActivityProcessBridge() {
        super();
    }

    public static ActivityProcessBridge getDefault() {
        if (instance == null) {
            synchronized (ActivityProcessBridge.class) {
                if (instance == null) {
                    instance = new ActivityProcessBridge();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        super.init();
        if (mMainProcess) {
            ProcessBridge.register(ActivityService.class);
            api = ActivityService.getInstance();
        } else {
            ProcessBridge.register(ActivityList.class);
        }
        Utils.getApp().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                ActivityList.getInstance().add(activity);
                actionInternal(new Action<IActivityService>() {
                    @Override
                    public void call(IActivityService o) {
                        o.add(pid, new ActivityInfoBean(activity));
                    }
                });
            }

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
            public void onActivityDestroyed(final Activity activity) {
                ActivityList.getInstance().remove(activity);
                actionInternal(new Action<IActivityService>() {
                    @Override
                    public void call(IActivityService o) {
                        o.remove(pid, new ActivityInfoBean(activity));
                    }
                });
            }
        });
    }

    @Override
    public void onProcessBridgeConnected(Class<? extends ProcessBridgeService> service) {
        IActivityService activityService = ProcessBridge.getInstanceInService(service, IActivityService.class);
        IActivityList activityList = ActivityList.getInstance();
        activityService.register(pid, activityList);
        mRemoteApis.set(activityService);
        mState = STATE_CONNECTED;
    }

    @Override
    public void onProcessBridgeDisconnected(Class<? extends ProcessBridgeService> service) {
        mState = STATE_DISCONNECTED;
        mRemoteApis.action(new Action<IActivityService>() {
            @Override
            public void call(IActivityService o) {
                o.unregister(pid);
            }
        });
    }

    /**
     * 获取所有进程所有activity数量
     *
     * @return
     */
    public int size() throws Exception {
        return calculateInternal(new Function<IActivityService, Integer>() {
            @Override
            public Integer call(IActivityService o) {
                return o.size();
            }
        });
    }

    /**
     * 获取某个进程所有activity数量
     *
     * @param pid
     * @return
     */
    public int size(final int pid) throws Exception {
        if (pid == this.pid) return ActivityList.getInstance().size();
        return calculateInternal(new Function<IActivityService, Integer>() {
            @Override
            public Integer call(IActivityService o) {
                return o.size(pid);
            }
        });
    }

    /**
     * 在所有进程中是否有匹配cls的activity
     *
     * @param cls
     * @return
     */
    public boolean contains(final Class cls) throws Exception {
        return calculateInternal(new Function<IActivityService, Boolean>() {
            @Override
            public Boolean call(IActivityService o) {
                return o.contains(cls.getName());
            }
        });
    }

    /**
     * 在某个进程中是否有匹配cls的activity
     *
     * @param pid
     * @param cls
     * @return
     */
    public boolean contains(final int pid, final Class cls) throws Exception {
        return calculateInternal(new Function<IActivityService, Boolean>() {
            @Override
            public Boolean call(IActivityService o) {
                return o.contains(pid, cls.getName());
            }
        });
    }

    /**
     * 获取最近的匹配cls的activity，只能获取当前进程的activity，否则会抛异常
     *
     * @param cls
     * @return
     */
    public Activity get(Class cls) {
        return ActivityList.getInstance().get(cls);
    }

    /**
     * 获取activity，只能获取当前进程的activity，否则会抛异常
     *
     * @param index
     * @return
     */
    public Activity get(int index) {
        return ActivityList.getInstance().get(index);
    }

    /**
     * finish所有进程的cls匹配activity
     *
     * @param cls
     */
    public void finish(final Class cls) {
        actionInternal(new Action<IActivityService>() {
            @Override
            public void call(IActivityService o) {
                o.finish(cls.getName());
            }
        });
    }

    /**
     * finish某个进程所有的activity
     *
     * @param pid
     */
    public void exit(final int pid) {
        actionInternal(new Action<IActivityService>() {
            @Override
            public void call(IActivityService o) {
                o.exit(pid);
            }
        });
    }

    /**
     * finish所有进程的所有activity
     *
     */
    public void exit() {
        actionInternal(new Action<IActivityService>() {
            @Override
            public void call(IActivityService o) {
                o.exit();
            }
        });
    }
}
