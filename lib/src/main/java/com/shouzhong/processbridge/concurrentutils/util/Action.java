package com.shouzhong.processbridge.concurrentutils.util;

public interface Action<T> {
    void call(T o);
}
