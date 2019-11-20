package com.shouzhong.processbridge.concurrentutils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentQueue<T> {

    private final ConcurrentLinkedQueue<T> queue;

    public ConcurrentQueue() {
        queue = new ConcurrentLinkedQueue<T>();
    }

    public T poll() {
        synchronized (this) {
            return queue.poll();
        }
    }

    public boolean offerIfNotEmpty(T e) {
        synchronized (this) {
            return !queue.isEmpty() && queue.offer(e);
        }
    }

    public boolean offer(T e) {
        synchronized (this) {
            return queue.offer(e);
        }
    }
}
