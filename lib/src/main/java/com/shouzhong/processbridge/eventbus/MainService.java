package com.shouzhong.processbridge.eventbus;

import com.shouzhong.processbridge.util.Utils;
import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.annotation.GetInstance;
import com.shouzhong.processbridge.base.annotation.MethodId;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ClassId("MainService")
class MainService implements IMainService {

    private static volatile MainService sInstance = null;

    private ConcurrentHashMap<Integer, ISubService> mSubServices;
    private ConcurrentHashMap<Integer, Set<Class>> eventTypes;
    private Set<Class> stickyEventClass;
    private EventBus mEventBus;

    private MainService() {
        mEventBus = EventBus.getDefault();
        mSubServices = new ConcurrentHashMap<>();
        stickyEventClass = new HashSet<>();
        eventTypes = new ConcurrentHashMap<>();
    }

    @GetInstance
    public static MainService getInstance() {
        if (sInstance == null) {
            synchronized (MainService.class) {
                if (sInstance == null) {
                    sInstance = new MainService();
                }
            }
        }
        return sInstance;
    }

    @MethodId("register")
    @Override
    public void register(int pid, ISubService subService) {
        mSubServices.put(pid, subService);
    }

    @MethodId("unregister")
    @Override
    public void unregister(int pid) {
        mSubServices.remove(pid);
    }

    @MethodId("post")
    @Override
    public void post(Object event) {
        mEventBus.post(event);
        removeDeadProcess();
        for (ISubService subService : mSubServices.values()) {
            subService.post(event);
        }
    }

    @MethodId("removeStickyEvent(Object)")
    @Override
    public boolean removeStickyEvent(Object event) {
        stickyEventClass.remove(event.getClass());
        boolean b = mEventBus.removeStickyEvent(event);
        removeDeadProcess();
        for (ISubService subService : mSubServices.values()) {
            subService.removeStickyEvent(event);
        }
        return b;
    }

    @MethodId("removeAllStickyEvents")
    @Override
    public void removeAllStickyEvents() {
        stickyEventClass.clear();
        mEventBus.removeAllStickyEvents();
        removeDeadProcess();
        for (ISubService subService : mSubServices.values()) {
            subService.removeAllStickyEvents();
        }
    }

    @MethodId("cancelEventDelivery")
    @Override
    public void cancelEventDelivery(Object event) {
        mEventBus.cancelEventDelivery(event);
        removeDeadProcess();
        for (ISubService subService : mSubServices.values()) {
            subService.cancelEventDelivery(event);
        }
    }

    @MethodId("postSticky")
    @Override
    public void postSticky(Object event) {
        stickyEventClass.add(event.getClass());
        mEventBus.postSticky(event);
        removeDeadProcess();
        for (ISubService subService : mSubServices.values()) {
            subService.postSticky(event);
        }
    }

    @MethodId("getStickyEvent(Class)")
    @Override
    public Object getStickyEvent(String clsName) {
        try {
            return mEventBus.getStickyEvent(Class.forName(clsName));
        } catch (Exception e) {}
        return null;
    }

    @MethodId("removeStickyEvent(Class)")
    @Override
    public Object removeStickyEvent(String clsName) {
        Object obj = null;
        try {
            stickyEventClass.remove(Class.forName(clsName));
            obj = mEventBus.removeStickyEvent(Class.forName(clsName));
            removeDeadProcess();
        } catch (Exception e) {}
        for (ISubService subService : mSubServices.values()) {
            subService.removeStickyEvent(clsName);
        }
        return obj;
    }

    @MethodId("getStickyEvent")
    @Override
    public List<Object> getStickyEvent() {
        if (stickyEventClass.size() == 0) return null;
        List<Object> list = null;
        for (Class cls : stickyEventClass) {
            Object obj = mEventBus.getStickyEvent(cls);
            if (obj == null) continue;
            if (list == null) list = new ArrayList<>();
            list.add(obj);
        }
        return list;
    }

    @MethodId("hasSubscriberForEvent")
    public boolean hasSubscriberForEvent(String clsName) {
        try {
            Class cls = Class.forName(clsName);
            for (Set<Class> set : eventTypes.values()) {
                if (set.contains(cls)) return true;
            }
        } catch (Exception e) {}
        return false;
    }

    @MethodId("putEventType")
    @Override
    public void putEventType(int id, String clsName) {
        try {
            Class cls = Class.forName(clsName);
            Set<Class> set = eventTypes.get(id);
            if (set == null) set = new HashSet<>();
            set.add(cls);
            eventTypes.put(id, set);
        } catch (Exception e) {}
    }

    @MethodId("removeEventType")
    @Override
    public void removeEventType(int id) {
        eventTypes.remove(id);
    }

    private void removeDeadProcess() {
        if (mSubServices.size() == 0) return;
        Enumeration<Integer> keys = mSubServices.keys();
        while (keys.hasMoreElements()) {
            int pid = keys.nextElement();
            if (!Utils.isProcessExist(pid)) {
                mSubServices.remove(pid);
            }
        }
    }
}
