package com.shouzhong.processbridge.concurrentutils.util;

public class IdenticalFunction<T> implements Function<T, T> {
    public T call(T o) {
        return o;
    }
}
