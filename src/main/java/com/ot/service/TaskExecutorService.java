package com.ot.service;

import com.ot.task.Task;
import com.ot.executor.TaskExecutor;
import com.ot.task.TaskGroup;
import com.ot.task.TaskWrapper;

import java.util.Map;
import java.util.concurrent.*;

public class TaskExecutorService implements TaskExecutor {

    private final ExecutorService executorService;
    private final BlockingQueue<TaskWrapper> taskQueue;
    private final Map<TaskGroup, Semaphore> taskGroupLocks;

    public TaskExecutorService(int maxConcurrency) {
        this.executorService = Executors.newFixedThreadPool(maxConcurrency);
        this.taskQueue = new LinkedBlockingQueue<>();
        this.taskGroupLocks = new ConcurrentHashMap<>();
        startTaskDispatcher();
    }

    private void startTaskDispatcher() {
        new Thread(() -> {
            try {
                while (!executorService.isShutdown()) {
                    TaskWrapper taskWrapper = taskQueue.take();
                    TaskGroup taskGroup = taskWrapper.getTask().taskGroup();
                    taskGroupLocks.put(taskGroup, new Semaphore(1));
                    Semaphore semaphore = taskGroupLocks.get(taskGroup);
                    semaphore.acquire();
                    executorService.submit(() -> {
                        try {
                            taskWrapper.run();
                        } finally {
                            semaphore.release();
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "ThreadA").start();

    }

    @Override
    public <T>Future<T> submitTask(Task<T> task) {
        TaskWrapper taskWrapper = new TaskWrapper(task);
        taskQueue.offer(taskWrapper);
        return (Future<T>) taskWrapper.getFuture();
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
