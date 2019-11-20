package com.shouzhong.processbridge.concurrentutils.util;

public class NonNullCondition<T> implements Condition<T>{

    @Override
    public boolean satisfy(T o) {
        return o != null;
    }
}
