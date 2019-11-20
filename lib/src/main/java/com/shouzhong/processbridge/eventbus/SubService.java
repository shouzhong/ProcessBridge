package com.shouzhong.processbridge.eventbus;

import com.shouzhong.processbridge.base.annotation.MethodId;

import org.greenrobot.eventbus.EventBus;

class SubService implements ISubService {

    private static volatile SubService sInstance = null;

    private EventBus mEventBus;

    private SubService() {
        mEventBus = EventBus.getDefault();
    }

    public static SubService getInstance() {
        if (sInstance == null) {
            synchronized (SubService.class) {
                if (sInstance == null) {
                    sInstance = new SubService();
                }
            }
        }
        return sInstance;
    }

    @MethodId("post")
    @Override
    public void post(Object event) {
        mEventBus.post(event);
    }

    @MethodId("postSticky")
    @Override
    public void postSticky(Object event) {
        mEventBus.postSticky(event);
    }

    @MethodId("cancelEventDelivery")
    @Override
    public void cancelEventDelivery(Object event) {
        mEventBus.cancelEventDelivery(event);
    }

    @MethodId("removeStickyEvent(Object)")
    @Override
    public void removeStickyEvent(Object event) {
        mEventBus.removeStickyEvent(event);
    }

    @MethodId("removeAllStickyEvents")
    @Override
    public void removeAllStickyEvents() {
        mEventBus.removeAllStickyEvents();
    }

    @MethodId("removeStickyEvent(Class)")
    @Override
    public void removeStickyEvent(String clsName) {
        try {
            mEventBus.removeStickyEvent(Class.forName(clsName));
        } catch (Exception e) {}
    }
}
