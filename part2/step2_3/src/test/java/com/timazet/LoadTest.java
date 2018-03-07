package com.timazet;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoadTest {

    private static final Logger log = LoggerFactory.getLogger(LoadTest.class);

    private static final String URL = "http://localhost:8080/hello";
    private static final int INVOCATIONS_NUMBER = 4;
    private static final int WORKERS_COUNT = 25;

    /**
     * It only works with specified <i>Tomcat Server</i> configuration, because <i>tomcat7-maven-plugin</i> refused to
     * take into account specified <i>server.xml</i> file.<br/>
     * If you specify <b>acceptCount</b>, which is equal or greater than {@link LoadTest#WORKERS_COUNT} - there won't be any rejected request.
     * Tomcat connection pool can cope with that load even with one thread, because there is enough space to place all simultaneous requests.<br/>
     * If you specify <b>acceptCount</b>, which is less than {@link LoadTest#WORKERS_COUNT} - there will be some probability that part of requests are rejected,
     * and it will be higher, the lesser the <b>acceptCount</b> and the <b>maxThreads</b>.<br/>
     * But we should also take into account the fight of threads for resources (available time of processor).
     * Results can be better if we increase <b>acceptCount</b> and decrease <b>maxThreads</b> instead of increasing <b>maxThreads</b>.
     */
    @Test
    public void test() {
        ExecutorService executor = Executors.newFixedThreadPool(WORKERS_COUNT);
        AtomicInteger rejectedRequests = new AtomicInteger(0);

        List<Future<?>> results = IntStream.range(0, WORKERS_COUNT).boxed().map(workerNumber -> executor.submit(() -> {
            try (CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build()) {
                for (int i = 0; i < INVOCATIONS_NUMBER; i++) {
                    try {
                        Files.streamToString(client.execute(new HttpGet(URL)).getEntity().getContent());
                    } catch (IOException e) {
                        rejectedRequests.incrementAndGet();
                    }
                }
            } catch (IOException e) {
                log.error("Error occurred during closing resource: {}", e.getMessage());
            }
        })).collect(Collectors.toList());

        results.forEach(result -> {
            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error occurred during getting result", e);
            }
        });

        log.info("There were {} rejected requests from {} (count of workers - {}, number of consecutive invocations - {})",
                rejectedRequests.get(), WORKERS_COUNT * INVOCATIONS_NUMBER, WORKERS_COUNT, INVOCATIONS_NUMBER);
    }

}
