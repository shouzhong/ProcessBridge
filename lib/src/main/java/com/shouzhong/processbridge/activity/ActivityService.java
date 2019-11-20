package com.shouzhong.processbridge.activity;

import android.os.Process;
import android.text.TextUtils;

import com.shouzhong.processbridge.util.Utils;
import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.annotation.GetInstance;
import com.shouzhong.processbridge.base.annotation.MethodId;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ClassId("ActivityService")
class ActivityService implements IActivityService {

    private static volatile ActivityService instance;

    private ConcurrentHashMap<Integer, List<ActivityInfoBean>> map;
    private ConcurrentHashMap<Integer, IActivityList> activityLists;
    private ActivityList activityList;
    private int pid;

    private ActivityService() {
        pid = Process.myPid();
        map = new ConcurrentHashMap<>();
        activityLists = new ConcurrentHashMap<>();
        activityList = ActivityList.getInstance();
    }

    @GetInstance
    public static ActivityService getInstance() {
        if (instance == null) {
            synchronized (ActivityService.class) {
                if (instance == null) {
                    instance = new ActivityService();
                }
            }
        }
        return instance;
    }

    @MethodId("register")
    @Override
    public void register(int pid, IActivityList activityList) {
        activityLists.put(pid, activityList);
    }

    @MethodId("unregister")
    @Override
    public void unregister(int pid) {
        activityLists.remove(pid);
        map.remove(pid);
    }

    @MethodId("size")
    @Override
    public int size() {
        removeDeadProcess();
        int size = 0;
        for (List<ActivityInfoBean> list : map.values()) {
            size += list.size();
        }
        return size;
    }

    @MethodId("size(int)")
    @Override
    public int size(int pid) {
        removeDeadProcess();
        List<ActivityInfoBean> list = map.get(pid);
        return list == null ? 0 : list.size();
    }

    @MethodId("add")
    @Override
    public void add(int pid, ActivityInfoBean b) {
        removeDeadProcess();
        List<ActivityInfoBean> list = map.get(pid);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(b);
        map.put(pid, list);
    }

    @MethodId("remove")
    @Override
    public void remove(int pid, ActivityInfoBean b) {
        removeDeadProcess();
        List<ActivityInfoBean> list = map.get(pid);
        if (list != null) {
            list.remove(b);
        }
    }

    @MethodId("contains")
    @Override
    public boolean contains(String className) {
        removeDeadProcess();
        for (List<ActivityInfoBean> list : map.values()) {
            for (ActivityInfoBean b : list) {
                if (TextUtils.equals(className, b.className)) return true;
            }
        }
        return false;
    }

    @MethodId("contains(int)")
    @Override
    public boolean contains(int pid, String className) {
        removeDeadProcess();
        List<ActivityInfoBean> list = map.get(pid);
        if (list == null) return false;
        for (ActivityInfoBean b : list) {
            if (TextUtils.equals(className, b.className)) return true;
        }
        return false;
    }

    @MethodId("finish")
    @Override
    public void finish(String className) {
        removeDeadProcess();
        activityList.finish(className);
        for (IActivityList activityList : activityLists.values()) {
            activityList.finish(className);
        }
    }

    @MethodId("exit(int)")
    @Override
    public void exit(int pid) {
        removeDeadProcess();
        if (this.pid == pid) activityList.exit();
        else {
            IActivityList activityList = activityLists.get(pid);
            if (activityList != null) activityList.exit();
        }
    }

    @MethodId("exit")
    @Override
    public void exit() {
        removeDeadProcess();
        activityList.exit();
        for (IActivityList activityList : activityLists.values()) {
            activityList.exit();
        }
    }

    private void removeDeadProcess() {
        if (map.size() == 0) return;
        Enumeration<Integer> keys = map.keys();
        while (keys.hasMoreElements()) {
            int pid = keys.nextElement();
            if (!Utils.isProcessExist(pid)) {
                map.remove(pid);
            }
        }
    }
}
