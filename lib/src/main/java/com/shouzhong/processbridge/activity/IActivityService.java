package com.shouzhong.processbridge.activity;

import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.annotation.MethodId;

@ClassId("ActivityService")
interface IActivityService {

    @MethodId("register")
    void register(int pid, IActivityList activityList);

    @MethodId("unregister")
    void unregister(int pid);

    @MethodId("size")
    int size();

    @MethodId("size(int)")
    int size(int pid);

    @MethodId("add")
    void add(int pid, ActivityInfoBean b);

    @MethodId("remove")
    void remove(int pid, ActivityInfoBean b);

    @MethodId("contains")
    boolean contains(String className);

    @MethodId("contains(int)")
    boolean contains(int pid, String className);

    @MethodId("finish")
    void finish(String className);

    @MethodId("exit(int)")
    void exit(int pid);

    @MethodId("exit")
    void exit();
}
