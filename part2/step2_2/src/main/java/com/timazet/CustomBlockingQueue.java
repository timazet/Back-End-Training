package com.timazet;

import java.util.LinkedList;
import java.util.Queue;

public class CustomBlockingQueue<E> {

    private final Queue<E> queue = new LinkedList<>();
    private final int limit;

    public CustomBlockingQueue(int limit) {
        this.limit = limit;
    }

    public synchronized void enqueue(E element) throws InterruptedException {
        while (queue.size() == limit) {
            wait();
        }
        if (queue.isEmpty()) {
            notifyAll();
        }
        queue.add(element);
    }

    public synchronized E dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        if (queue.size() == limit) {
            notifyAll();
        }
        return queue.poll();
    }

    public synchronized int remainingCapacity() {
        return limit - queue.size();
    }

    public synchronized int size() {
        return queue.size();
    }

}
