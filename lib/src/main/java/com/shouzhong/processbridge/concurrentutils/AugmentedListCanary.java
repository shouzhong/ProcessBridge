package com.shouzhong.processbridge.concurrentutils;

import com.shouzhong.processbridge.concurrentutils.util.Action;
import com.shouzhong.processbridge.concurrentutils.util.Condition;
import com.shouzhong.processbridge.concurrentutils.util.Function;
import com.shouzhong.processbridge.concurrentutils.util.NonNullCondition;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AugmentedListCanary<T> {

    private final Condition<T> nonNullCondition = new NonNullCondition<T>();

    private volatile CopyOnWriteArrayList<T> list;

    private final CopyOnWriteArrayList<Lock> locks;

    private final CopyOnWriteArrayList<java.util.concurrent.locks.Condition> conditions;

    public AugmentedListCanary() {
        list = new CopyOnWriteArrayList<T>();
        locks = new CopyOnWriteArrayList<Lock>();
        conditions = new CopyOnWriteArrayList<java.util.concurrent.locks.Condition>();
    }

    public int add(T o) {
        synchronized (this) {
            list.add(o);
            Lock lock = new ReentrantLock();
            locks.add(lock);
            conditions.add(lock.newCondition());
            return list.size();
        }
    }

    public int size() {
        return list.size();
    }

    public T getNonNull(int index) {
        return get(index, nonNullCondition);
    }

    public T get(int index, Condition<? super T> condition) {
        T result = null;
        locks.get(index).lock();
        try {
            while (!condition.satisfy(result = list.get(index))) {
                conditions.get(index).await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            locks.get(index).unlock();
        }
        return result;
    }

    public T get(int index) {
        return list.get(index);
    }

    public <R extends T> void set(int index, R o) {
        locks.get(index).lock();
        list.set(index, o);
        conditions.get(index).signalAll();
        locks.get(index).unlock();
    }

    public T getAndSet(int index, Function<? super T, ? extends T> function) {
        T result;
        locks.get(index).lock();
        list.set(index, function.call(result = list.get(index)));
        conditions.get(index).signalAll();
        locks.get(index).unlock();
        return result;
    }

    public void action(int index, Action<? super T> action) {
        locks.get(index).lock();
        action.call(list.get(index));
        conditions.get(index).signalAll();
        locks.get(index).unlock();
    }

    public void wait(int index, Condition<? super T> condition) {
        locks.get(index).lock();
        try {
            while (!condition.satisfy(list.get(index))) {
                conditions.get(index).await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            locks.get(index).unlock();
        }
    }

    public boolean satisfy(int index, Condition<? super T> condition) {
        locks.get(index).lock();
        boolean result = condition.satisfy(list.get(index));
        locks.get(index).unlock();
        return result;
    }

}
