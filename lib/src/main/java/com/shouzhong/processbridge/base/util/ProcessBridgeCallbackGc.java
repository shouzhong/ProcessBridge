package com.shouzhong.processbridge.base.util;

import android.os.RemoteException;

import androidx.core.util.Pair;

import com.shouzhong.processbridge.base.internal.IProcessBridgeServiceCallback;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * This works in the main process.
 */
public class ProcessBridgeCallbackGc {

    private static volatile ProcessBridgeCallbackGc sInstance = null;

    private final ReferenceQueue<Object> mReferenceQueue;

    private final ConcurrentHashMap<PhantomReference<Object>, Triple<IProcessBridgeServiceCallback, Long, Integer>> mTimeStamps;

    private ProcessBridgeCallbackGc() {
        mReferenceQueue = new ReferenceQueue<>();
        mTimeStamps = new ConcurrentHashMap<>();
    }

    public static ProcessBridgeCallbackGc getInstance() {
        if (sInstance == null) {
            synchronized (ProcessBridgeCallbackGc.class) {
                if (sInstance == null) {
                    sInstance = new ProcessBridgeCallbackGc();
                }
            }
        }
        return sInstance;
    }

    private void gc() {
        synchronized (mReferenceQueue) {
            PhantomReference<Object> reference;
            Triple<IProcessBridgeServiceCallback, Long, Integer> triple;
            HashMap<IProcessBridgeServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>> timeStamps = new HashMap<>();
            while ((reference = (PhantomReference<Object>) mReferenceQueue.poll()) != null) {
                triple = mTimeStamps.remove(reference);
                if (triple != null) {
                    Pair<ArrayList<Long>, ArrayList<Integer>> tmp = timeStamps.get(triple.first);
                    if (tmp == null) {
                        tmp = new Pair<>(new ArrayList<Long>(), new ArrayList<Integer>());
                        timeStamps.put(triple.first, tmp);
                    }
                    tmp.first.add(triple.second);
                    tmp.second.add(triple.third);
                }
            }
            Set<Map.Entry<IProcessBridgeServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>>> set = timeStamps.entrySet();
            for (Map.Entry<IProcessBridgeServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>> entry : set) {
                Pair<ArrayList<Long>, ArrayList<Integer>> values = entry.getValue();
                if (!values.first.isEmpty()) {
                    try {
                        entry.getKey().gc(values.first, values.second);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void register(IProcessBridgeServiceCallback callback, Object object, long timeStamp, int index) {
        gc();
        mTimeStamps.put(new PhantomReference<>(object, mReferenceQueue), Triple.create(callback, timeStamp, index));
    }
}
