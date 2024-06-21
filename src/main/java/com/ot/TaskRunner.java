package com.ot;

import com.ot.service.TaskExecutorService;
import com.ot.model.Task;
import com.ot.model.TaskGroup;
import com.ot.model.TaskType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskRunner {

    private static final int CONCURRENCY_LEVEL = 4;
    private static final int THREAD_SLEEP_TIME_IN_MILLIS = 2000;

    public static void main(String[] args) throws InterruptedException {
        LocalTime startTime = LocalTime.now();
        System.out.println("Task Runner Starting ... ");

        TaskExecutorService taskExecutorService = new TaskExecutorService(CONCURRENCY_LEVEL);
        TaskGroup taskGroup1 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup2 = new TaskGroup(UUID.randomUUID());
        List<Future<String>> futureResultList = new ArrayList<>();

        // TaskGroup 1
        for (int i = 1; i <= 5; i++) {
            String taskName = "TASK" + i;
            Task<String> task1 = new Task<>(UUID.randomUUID(), taskGroup1, TaskType.READ, () -> {
                String s = ("Submit => Task [TG1_" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by " + Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(THREAD_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureResultList.add(taskExecutorService.submitTask(task1));
        }

        // TaskGroup 2
        for (int j = 1; j <= 5; j++) {
            String taskName = "TASK" + j;
            Task<String> task2 = new Task<>(UUID.randomUUID(), taskGroup2, TaskType.WRITE, () -> {
                String s = ("Submit => Task [TG2_" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by " + Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(THREAD_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureResultList.add(taskExecutorService.submitTask(task2));
        }

        // Print Result
        futureResultList.forEach(f -> {
            try {
                System.out.println(f.get().replace("Submit => ", "Result => "));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        // Log Stats
        LocalTime endTime = LocalTime.now();
        System.out.println("Task Runner Stopping ... ");
        System.out.println("*********************************************************************");
        System.out.println("INFO => Task Runner Started @ " + startTime);
        System.out.println("INFO => Task Runner Completed @ " + endTime);
        System.out.println("INFO => Total Time Elapsed = " + ChronoUnit.MILLIS.between(startTime, endTime) + "ms");
        System.out.println("INFO => Total task Executed = " + futureResultList.size() + " , Concurrency Level = " + CONCURRENCY_LEVEL);
        System.out.println("*********************************************************************");
        taskExecutorService.shutDown();
    }
}
