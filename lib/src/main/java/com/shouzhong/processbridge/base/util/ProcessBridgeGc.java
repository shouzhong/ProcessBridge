package com.shouzhong.processbridge.base.util;

import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.internal.Channel;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * This works in the remote process.
 */
public class ProcessBridgeGc {

    private static volatile ProcessBridgeGc sInstance = null;

    private final ReferenceQueue<Object> mReferenceQueue;

    private static final Channel CHANNEL = Channel.getInstance();

    private final ConcurrentHashMap<PhantomReference<Object>, Long> mTimeStamps;

    private final ConcurrentHashMap<Long, Class<? extends ProcessBridgeService>> mServices;

    private ProcessBridgeGc() {
        mReferenceQueue = new ReferenceQueue<>();
        mTimeStamps = new ConcurrentHashMap<>();
        mServices = new ConcurrentHashMap<>();
    }

    public static ProcessBridgeGc getInstance() {
        if (sInstance == null) {
            synchronized (ProcessBridgeGc.class) {
                if (sInstance == null) {
                    sInstance = new ProcessBridgeGc();
                }
            }
        }
        return sInstance;
    }

    private void gc() {
        synchronized (mReferenceQueue) {
            PhantomReference<Object> reference;
            Long timeStamp;
            HashMap<Class<? extends ProcessBridgeService>, ArrayList<Long>> timeStamps = new HashMap<>();
            while ((reference = (PhantomReference<Object>) mReferenceQueue.poll()) != null) {
                //After a long time, the program can reach here.
                timeStamp = mTimeStamps.remove(reference);
                if (timeStamp != null) {
                    Class<? extends ProcessBridgeService> clazz = mServices.remove(timeStamp);
                    if (clazz != null) {
                        ArrayList<Long> tmp = timeStamps.get(clazz);
                        if (tmp == null) {
                            tmp = new ArrayList<Long>();
                            timeStamps.put(clazz, tmp);
                        }
                        tmp.add(timeStamp);
                    }
                }
            }
            Set<Map.Entry<Class<? extends ProcessBridgeService>, ArrayList<Long>>> set = timeStamps.entrySet();
            for (Map.Entry<Class<? extends ProcessBridgeService>, ArrayList<Long>> entry : set) {
                ArrayList<Long> values = entry.getValue();
                if (!values.isEmpty()) {
                    CHANNEL.gc(entry.getKey(), values);
                }
            }
        }
    }

    public void register(Class<? extends ProcessBridgeService> service, Object object, Long timeStamp) {
        gc();
        mTimeStamps.put(new PhantomReference<>(object, mReferenceQueue), timeStamp);
        mServices.put(timeStamp, service);
    }
}
