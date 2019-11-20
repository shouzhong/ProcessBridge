package com.shouzhong.processbridge.eventbus;

import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.annotation.MethodId;

import java.util.List;

@ClassId("MainService")
interface IMainService {

    @MethodId("register")
    void register(int pid, ISubService subService);

    @MethodId("unregister")
    void unregister(int pid);

    @MethodId("post")
    void post(Object event);

    @MethodId("postSticky")
    void postSticky(Object event);

    @MethodId("cancelEventDelivery")
    void cancelEventDelivery(Object event);

    @MethodId("getStickyEvent(Class)")
    Object getStickyEvent(String clsName);

    @MethodId("removeStickyEvent(Class)")
    Object removeStickyEvent(String clsName);

    @MethodId("removeStickyEvent(Object)")
    boolean removeStickyEvent(Object event);

    @MethodId("removeAllStickyEvents")
    void removeAllStickyEvents();

    @MethodId("getStickyEvent")
    List<Object> getStickyEvent();

    @MethodId("hasSubscriberForEvent")
    boolean hasSubscriberForEvent(String clsName);

    @MethodId("putEventType")
    void putEventType(int id, String clsName);

    @MethodId("removeEventType")
    void removeEventType(int id);
}
