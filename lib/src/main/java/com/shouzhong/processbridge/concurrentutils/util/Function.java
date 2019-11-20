package com.shouzhong.processbridge.concurrentutils.util;

public interface Function<T, R> {
    R call(T o);
}
