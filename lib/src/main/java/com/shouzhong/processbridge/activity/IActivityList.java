package com.shouzhong.processbridge.activity;

import com.shouzhong.processbridge.base.annotation.MethodId;

interface IActivityList {

    @MethodId("finish")
    void finish(String className);

    @MethodId("exit")
    void exit();
}
