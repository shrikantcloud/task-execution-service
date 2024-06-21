package com.ot.service;

import com.ot.executor.TaskExecutor;
import com.ot.model.Task;
import com.ot.model.TaskAdaptor;
import com.ot.model.TaskGroup;

import java.util.Map;
import java.util.concurrent.*;

public class TaskExecutorService implements TaskExecutor {

    private static final int SEMAPHORE_INITIAL_PERMIT_COUNT = 1;
    private final ExecutorService executorService;
    private final BlockingQueue<TaskAdaptor> taskQueue;
    private final Map<TaskGroup, Semaphore> taskGroupLocks;

    public TaskExecutorService(int maxConcurrency) {
        this.executorService = Executors.newFixedThreadPool(maxConcurrency);
        this.taskQueue = new LinkedBlockingQueue<>();
        this.taskGroupLocks = new ConcurrentHashMap<>();
        dispatchTask();
    }

    private void dispatchTask() {

        new Thread(() -> {
            try {
                while (!executorService.isShutdown()) {
                    final Semaphore semaphore;
                    TaskAdaptor taskAdaptor = taskQueue.take();
                    TaskGroup taskGroup = taskAdaptor.getTask().taskGroup();
                    if (!taskGroupLocks.containsKey(taskGroup)) {
                        taskGroupLocks.put(taskGroup, new Semaphore(SEMAPHORE_INITIAL_PERMIT_COUNT, true));
                    }
                    semaphore = taskGroupLocks.get(taskGroup);
                    semaphore.acquire();
                    executorService.submit(() -> {
                        try {
                            taskAdaptor.run();
                        } finally {
                            semaphore.release();
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        TaskAdaptor taskAdaptor = new TaskAdaptor(task);
        taskQueue.offer(taskAdaptor);
        return (Future<T>) taskAdaptor.getFuture();
    }

    public void shutDown() throws InterruptedException {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
