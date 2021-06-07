package com.java.batchqueu;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//dummy commit for cr
public class LimitedCachedThreadPool extends ThreadPoolExecutor {
    public LimitedCachedThreadPool(int limit) {
        super(0,
                limit,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }
}
