package com.shouzhong.processbridge.concurrentutils.util;

public class Tautology<T> implements Condition<T>{

    @Override
    public boolean satisfy(T o) {
        return true;
    }
}
