package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomThreadPoolExample {

    /**
     * If we don't terminate manually thread pool, our program will run forever due to the specific of blocking queue and the way how JVM handles user threads.
     */
    public static void main(String[] args) {
        CustomThreadPool executor = new CustomThreadPool(2, 4);
        IntStream.range(0, 10).forEach(i -> executor.execute(new SimpleTask(i)));
        executor.terminate();
    }

    /**
     * Custom implementation of thread pool using blocking queue
     */
    private static class CustomThreadPool {

        private static final Logger log = LoggerFactory.getLogger(CustomThreadPool.class);

        private final BlockingQueue<Runnable> queue;
        private final List<ThreadPoolWorker> workers;

        CustomThreadPool(int poolSize, int queueSize) {
            queue = new ArrayBlockingQueue<>(queueSize);
            workers = IntStream.range(0, poolSize).boxed().map(n -> new ThreadPoolWorker(queue, n))
                    .collect(Collectors.toList());
            workers.forEach(Thread::start);
        }

        void execute(Runnable task) {
            if (!queue.offer(task)) {
                log.info("Task was rejected due to the limits of queue");
            }
        }

        void terminate() {
            workers.forEach(ThreadPoolWorker::terminate);
        }

    }

    /**
     * Thread pool worker implementation
     */
    private static class ThreadPoolWorker extends Thread {

        private static final Logger log = LoggerFactory.getLogger(ThreadPoolWorker.class);

        private volatile boolean terminated = false;
        private volatile BlockingQueue<Runnable> queue;

        ThreadPoolWorker(BlockingQueue<Runnable> queue, int index) {
            this.queue = queue;
            setName("ThreadWorker-" + index);
        }

        @Override
        public void run() {
            while (!terminated) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    log.error("Worker was interrupted", e);
                }
            }
            log.info("Worker was terminated. Number of unprocessed tasks is '{}'", queue.size() - queue.remainingCapacity());
        }

        void terminate() {
            terminated = true;
        }

    }


}
