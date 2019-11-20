package com.shouzhong.processbridge.activity;

import android.app.Activity;

import com.shouzhong.processbridge.base.annotation.MethodId;

import java.util.ArrayList;
import java.util.List;

class ActivityList implements IActivityList {

    public static volatile ActivityList instance;

    private List<Activity> list;

    private ActivityList() {
        list = new ArrayList<>();
    }

    public static ActivityList getInstance() {
        if (instance == null) {
            synchronized (ActivityList.class) {
                if (instance == null) instance = new ActivityList();
            }
        }
        return instance;
    }

    public int size() {
        return list.size();
    }

    public void add(Activity activity) {
        list.add(activity);
    }

    public void remove(Activity activity) {
        list.remove(activity);
    }

    public boolean contains(Class cls) {
        for (Activity activity : list) {
            if (cls == activity.getClass()) return true;
        }
        return false;
    }

    public Activity get(Class cls) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (cls == list.get(i).getClass()) return list.get(i);
        }
        return null;
    }

    public Activity get(int index) {
        return list.get(index);
    }

    public void finish(Class cls) {
        for (Activity activity : list) {
            if (cls == activity.getClass()) activity.finish();
        }
    }

    @MethodId("finish")
    @Override
    public void finish(String className) {
        try {
            Class cls = Class.forName(className);
            finish(cls);
        } catch (Exception e) {}
    }

    @MethodId("exit")
    @Override
    public void exit() {
        for (Activity activity : list) {
            activity.finish();
        }
    }
}
