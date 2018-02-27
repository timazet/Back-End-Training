package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class FactorialTask extends RecursiveTask<BigInteger> {

    private static final Logger log = LoggerFactory.getLogger(FactorialTask.class);

    private static final int THRESHOLD = 5;
    private int start;
    private int n;

    FactorialTask(int n) {
        this.start = 1;
        this.n = n;
    }

    private FactorialTask(int start, int n) {
        this.start = start;
        this.n = n;
    }

    @Override
    protected BigInteger compute() {
        log.info("Calculate factorial from '{}' to '{}'", start, n);
        if (n - start > THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubtasks(start, n)).stream()
                    .map(ForkJoinTask::join)
                    .reduce(BigInteger.ONE, BigInteger::multiply);
        } else {
            return calculate(start, n);
        }
    }

    private BigInteger calculate(int start, int n) {
        return IntStream.rangeClosed(start, n)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);
    }

    private List<FactorialTask> createSubtasks(int start, int n) {
        int middle = (start + n) / 2;
        List<FactorialTask> subtasks = new ArrayList<>(2);
        subtasks.add(new FactorialTask(start, middle));
        subtasks.add(new FactorialTask(middle + 1, n));
        return subtasks;
    }

}
