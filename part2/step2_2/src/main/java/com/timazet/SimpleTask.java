package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SimpleTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SimpleTask.class);

    private int taskNumber;

    SimpleTask(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void run() {
        log.info("Task '{}' is executed", taskNumber);
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1L));
        } catch (InterruptedException e) {
            log.error("Error occurred during sleeping", e);
            Thread.currentThread().interrupt();
        }
    }

}
