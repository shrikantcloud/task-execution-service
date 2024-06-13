package com.ot;

import com.ot.service.TaskExecutorService;
import com.ot.task.Task;
import com.ot.task.TaskGroup;
import com.ot.task.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskRunner {

    private static final int CONCURRENCY_LEVEL = 3;
    private static final int THREAD_SLEEP_TIME_IN_MILLIS = 1000;
    private static final int MIN_THRESHOLD_COUNT = 1;
    private static final int MAX_THRESHOLD_COUNT = 5;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        TaskExecutorService executor = new TaskExecutorService(CONCURRENCY_LEVEL);
        TaskGroup taskGroup1 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup2 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup3 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup4 = new TaskGroup(UUID.randomUUID());
        List<Future<String>> futureList = new ArrayList<>();

        // Task 1
        for (int i = MIN_THRESHOLD_COUNT; i <= MAX_THRESHOLD_COUNT; i++) {
            String taskName = "Work for task " + i;
            Task<String> task1 = new Task<>(UUID.randomUUID(), taskGroup1, TaskType.READ, () -> {
                String s = ("TaskGroup1 => Task [" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by " + Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(THREAD_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureList.add(executor.submitTask(task1));
        }

        // Task 2
        for (int j = MIN_THRESHOLD_COUNT + 5; j <= MAX_THRESHOLD_COUNT + 5; j++) {
            String taskName = "Work for task " + j;
            Task<String> task2 = new Task<>(UUID.randomUUID(), taskGroup2, TaskType.WRITE, () -> {
                String s = ("TaskGroup2 => Task [" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by " + Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(THREAD_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureList.add(executor.submitTask(task2));
        }

        // Task 3
        for (int i = MIN_THRESHOLD_COUNT + 10; i <= MAX_THRESHOLD_COUNT + 10; i++) {
            String taskName = "Work for task " + i;
            Task<String> task3 = new Task<>(UUID.randomUUID(), taskGroup3, TaskType.READ, () -> {
                String s = ("TaskGroup3 => Task [" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by " + Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(THREAD_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureList.add(executor.submitTask(task3));
        }

        // Task 4
        for (int i = MIN_THRESHOLD_COUNT + 15; i <= MAX_THRESHOLD_COUNT + 15; i++) {
            String taskName = "Work for task " + i;
            Task<String> task4 = new Task<>(UUID.randomUUID(), taskGroup4, TaskType.READ, () -> {
                String s = ("TaskGroup4 => Task [" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by " + Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(THREAD_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureList.add(executor.submitTask(task4));
        }

        // Print Result
        futureList.forEach(f -> {
            try {
                System.out.println("future result => " + f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Completed Execution!");
        executor.shutDown();
    }
}
