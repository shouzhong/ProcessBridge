package com.shouzhong.processbridge.concurrentutils;

import android.util.Log;

import com.shouzhong.processbridge.concurrentutils.util.Action;
import com.shouzhong.processbridge.concurrentutils.util.Function;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectCanary2<T> {

    private final String TAG = "ObjectCanary2";

    private volatile T object;

    private final Lock lock;
    private final Condition condition;
    private final ExecutorService executor;

    public ObjectCanary2() {
        object = null;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        executor = Executors.newSingleThreadExecutor();
    }

    public void action(final Action<? super T> action) {
        if (object == null ) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (object == null) {
                        lock.lock();
                        try {
                            if (object == null) {
                                condition.await(5, TimeUnit.SECONDS);
                            }
                            if (object != null) action.call(object);
                            else Log.e(TAG, "obj is null");
                        } catch (Exception e) {
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        action.call(object);
                    }
                }
            });
        } else {
            action.call(object);
        }
    }

    public <R> R calculate(final Function<? super T, ? extends R> function) throws Exception {
        if (object == null ) {
            Future<R> future = executor.submit(new Callable<R>() {
                @Override
                public R call() throws Exception {
                    R result = null;
                    if (object == null) {
                        lock.lock();
                        try {
                            if (object == null) {
                                condition.await(5, TimeUnit.SECONDS);
                            }
                            if (object != null) result = function.call(object);
                        } catch (Exception e) {
                        } finally {
                            lock.unlock();
                        }
                        if (object == null) {
                            Log.e(TAG, "obj is null");
                            throw new Exception("obj is null");
                        }
                    } else {
                        result = function.call(object);
                    }
                    return result;
                }
            });
            return future.get();
        } else {
            return function.call(object);
        }
    }

    public void set(T object) {
        if (object == null) {
            throw new IllegalArgumentException("You cannot assign null to this object.");
        }
        lock.lock();
        try {
            this.object = object;
            condition.signalAll();
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }
}
