package com.shouzhong.processbridge.base;

import android.os.Process;
import android.util.Log;

import com.shouzhong.processbridge.concurrentutils.ObjectCanary2;
import com.shouzhong.processbridge.concurrentutils.util.Action;
import com.shouzhong.processbridge.concurrentutils.util.Function;
import com.shouzhong.processbridge.util.Utils;

public abstract class BaseProcessBridge<T> implements IProcessBridge {

    public final String TAG = getClass().getSimpleName();

    protected static final int STATE_DISCONNECTED = 0;
    protected static final int STATE_CONNECTING = 1;
    protected static final int STATE_CONNECTED = 2;

    protected volatile ObjectCanary2<T> mRemoteApis;
    protected volatile T api;
    protected volatile boolean mMainProcess;
    protected volatile int mState = STATE_DISCONNECTED;
    protected volatile int pid;

    protected BaseProcessBridge() {
        mRemoteApis = new ObjectCanary2<>();
    }

    public void init() {
        pid = Process.myPid();
        mMainProcess = Utils.isMainProcess();
        if (!mMainProcess) {
            mState = STATE_CONNECTING;
        }
    }

    protected void actionInternal(final Action<T> action) {
        if (mMainProcess) {
            action.call(api);
        } else {
            if (mState == STATE_DISCONNECTED) {
                Log.e(TAG, "service disconnected!");
            } else {
                mRemoteApis.action(new Action<T>() {
                    @Override
                    public void call(T o) {
                        action.call(o);
                    }
                });
            }
        }
    }

    protected  <K> K calculateInternal(final Function<T, ? extends K> function) throws Exception {
        if (mMainProcess) {
            return function.call(api);
        } else {
            if (mState == STATE_DISCONNECTED) {
                Log.e(TAG, "service disconnected!");
                throw new Exception("service disconnected!");
            } else {
                if (Utils.isMainThread() && !mMainProcess && mState == STATE_CONNECTING) {
                    Log.e(TAG, "service connecting, thread will block");
                    throw new Exception("service connecting, thread will block");
                }
                return mRemoteApis.calculate(new Function<T, K>() {
                    @Override
                    public K call(T o) {
                        return function.call(o);
                    }
                });
            }
        }
    }

}
