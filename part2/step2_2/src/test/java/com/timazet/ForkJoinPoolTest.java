package com.timazet;

import org.testng.annotations.Test;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolTest {

    /**
     * Through logs we can see how "work stealing" technique works.<br/>
     * We submit only one task and all other task that can be produced
     * will be placed into the internal thread worker queue, which is available for worker itself.<br/>
     * But there is another thing that other free workers can steal tasks from the end of the queue of another thread workers.<br/>
     * ForkJoinPool is recommended mostly for recursive tasks.
     */
    @Test
    public void test() {
        new ForkJoinPool(4).invoke(new FactorialTask(100));
    }

}
