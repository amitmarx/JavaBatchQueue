package com.java.batchqueu;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatchQueue<T> implements AutoCloseable {
    private final ArrayBlockingQueue<T> queue;
    private final Consumer<ArrayList<T>> callback;
    private final AtomicBoolean isCleanInProcess = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService callbackExecution = Executors.newSingleThreadExecutor();
    private final ExecutorService pushExecution = Executors.newFixedThreadPool(10);


    public BatchQueue(int sizeLimit, long timeLimit, Consumer<ArrayList<T>> callback) {
        this.queue = new ArrayBlockingQueue<>(sizeLimit);
        this.callback = callback;
        this.scheduleExecutor.scheduleWithFixedDelay(
                () -> this.cleanQueueIfNeeded(true),
                timeLimit,
                timeLimit,
                TimeUnit.MILLISECONDS);
    }

    public void push(T item) {
        boolean isAdded = queue.offer(item);
        if (!isAdded) {
            pushExecution.submit(() -> {
                cleanQueueIfNeeded(false);
                this.push(item);
            });
        }
    }

    private void cleanQueueIfNeeded(boolean ignoreCapacity) {
        boolean isFirstCleaning = this.isCleanInProcess.compareAndSet(false, true);
        if (isFirstCleaning) {
            if (ignoreCapacity || this.queue.remainingCapacity() == 0) {
                ArrayList<T> items = new ArrayList<>(this.queue.size());
                int numberOfItems = this.queue.drainTo(items);
                if (numberOfItems > 0) {
                    callbackExecution.submit(() -> this.callback.accept(items));
                }
            }
            this.isCleanInProcess.set(false);
        }
    }

    @Override
    public void close() {
        this.scheduleExecutor.shutdown();
        this.callbackExecution.shutdown();
        this.pushExecution.shutdown();
    }
}