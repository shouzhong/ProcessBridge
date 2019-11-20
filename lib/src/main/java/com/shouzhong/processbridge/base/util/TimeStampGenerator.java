package com.shouzhong.processbridge.base.util;

import java.util.concurrent.atomic.AtomicLong;

public class TimeStampGenerator {
    private static AtomicLong sTimeStamp = new AtomicLong();

    public static long getTimeStamp() {
        return sTimeStamp.incrementAndGet();
    }
}
