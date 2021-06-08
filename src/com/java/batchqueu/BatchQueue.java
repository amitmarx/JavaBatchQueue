package com.java.batchqueu;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatchQueue<T> implements AutoCloseable {
    private final Consumer<ArrayList<T>> callback;
    private final AtomicBoolean isCleanInProcess = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService callbackExecutor = Executors.newFixedThreadPool(5);
    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private final int sizeLimit;
    private final int overflow;


    public BatchQueue(int batchSize, long timeLimitInMillis, Consumer<ArrayList<T>> callback) {
        this.sizeLimit = batchSize;
        this.overflow = batchSize * 3;
        this.callback = callback;
        this.scheduleExecutor.scheduleAtFixedRate(
                () -> this.processBatchIfNeeded(true),
                timeLimitInMillis,
                timeLimitInMillis,
                TimeUnit.MILLISECONDS);
    }

    public void push(T item) {
        if (!this.isInOverflow()) {
            queue.add(item);
        }

        if (this.isBatchReady()) {
            processBatchIfNeeded(false);
        }
    }

    private boolean isInOverflow() {
        return queue.size() >= this.overflow;
    }

    private boolean isBatchReady() {
        return queue.size() >= this.sizeLimit;
    }

    private ArrayList<T> takeBatch() {
        ArrayList<T> batch = new ArrayList<>();
        for (int i = 0; i < this.sizeLimit; i++) {
            T maybeItem = this.queue.poll();
            if (maybeItem != null) {
                batch.add(maybeItem);
            }
        }
        return batch;
    }

    private void processBatchIfNeeded(boolean ignoreCapacity) {
        ArrayList<T> batch = null;

        boolean isFirstCleaning = this.isCleanInProcess.compareAndSet(false, true);
        if (isFirstCleaning) {
            if (ignoreCapacity || this.isBatchReady()) {
                batch = takeBatch();
            }
            this.isCleanInProcess.set(false);
        }

        if (batch != null && batch.size() > 0) {
            ArrayList<T> finalBatch = batch;
            callbackExecutor.submit(() -> this.callback.accept(finalBatch));

            if (isBatchReady()) {
                this.processBatchIfNeeded(false);
            }
        }
    }

    @Override
    public void close() {
        this.scheduleExecutor.shutdown();
        this.callbackExecutor.shutdown();
    }
}