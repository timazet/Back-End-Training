package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonExample {

    private static final Logger log = LoggerFactory.getLogger(DaemonExample.class);

    private static final boolean DAEMON = true;

    /**
     * Only the active user threads ({@code isDaemon = false}) prevent JVM from exiting.<br/>
     * In current case, if we specify {@link #DAEMON} as <b>false</b>, we'll never see the end of execution.
     * It means that we'll have the active user thread, which doesn't have a tend to stop.<br/>
     * But if we specify {@link #DAEMON} as <b>true</b>, the execution of active daemon thread
     * will be interrupted by JVM due to the fact that there won't be any active user thread.
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("Current thread is daemon - {}", Thread.currentThread().isDaemon());
            while (true) {
                try {
                    log.info("Going to sleep...");
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    log.error("Error occurred during thread sleeping");
                }
            }
        });

        thread.setDaemon(DAEMON);
        thread.start();

        Thread.sleep(4000L);
        log.info("Main thread reached the end");
    }

}
