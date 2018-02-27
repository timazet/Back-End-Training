package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThreadPoolTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolTest.class);

    /**
     * Through logs we can see how tasks are executed by configured pool.<br/>
     * Also we can see that some of them are rejected due to the fact that underlying blocking queue bounds will be exceeded.
     */
    @Test
    public void test() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 2L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4), (runnable, poolExecutor) -> {
            log.warn("Some runnable was rejected");
            ((Future) runnable).cancel(true);
        });

        List<Future<?>> results = IntStream.range(0, 10).mapToObj(i -> executor.submit(new SimpleTask(i)))
                .collect(Collectors.toList());
        results.stream().filter(result -> !result.isCancelled()).forEach(result -> {
            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error occurred during getting result", e);
            }
        });
    }

}
