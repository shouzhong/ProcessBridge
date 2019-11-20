package com.shouzhong.processbridge.concurrentutils.util;

public interface Condition<T> {
    boolean satisfy(T o);
}
