package com.shouzhong.processbridge.eventbus;

import com.shouzhong.processbridge.base.annotation.MethodId;

interface ISubService {

    @MethodId("post")
    void post(Object event);

    @MethodId("postSticky")
    void postSticky(Object event);

    @MethodId("cancelEventDelivery")
    void cancelEventDelivery(Object event);

    @MethodId("removeStickyEvent(Class)")
    void removeStickyEvent(String clsName);

    @MethodId("removeStickyEvent(Object)")
    void removeStickyEvent(Object event);

    @MethodId("removeAllStickyEvents")
    void removeAllStickyEvents();
}
