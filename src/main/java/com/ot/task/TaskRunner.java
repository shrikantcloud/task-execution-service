package com.ot.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskRunner {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        TaskExecutorService executor = new TaskExecutorService(2);
        TaskGroup taskGroup1 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup2 = new TaskGroup(UUID.randomUUID());
        List<Future<String>> futureList = new ArrayList<>();
          for(int i=1; i<=5;i++){
            String taskName = "Work for task "+i;
            Task<String> task1 = new Task<>(UUID.randomUUID(),taskGroup1,TaskType.READ, () -> {
                String s = ("TaskGroup1 => Task [" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by "+ Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureList.add(executor.submitTask(task1));
        }

        for(int j=6; j<=10;j++){
            String taskName = "Work for task "+j;
            Task<String> task2 = new Task<>(UUID.randomUUID(),taskGroup2,TaskType.WRITE, () -> {
                String s = ("TaskGroup2 => Task [" + taskName + "] executed on : " + LocalDateTime.now().toString() + " by "+ Thread.currentThread().getName());
                System.out.println(s);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return s;
            });
            futureList.add(executor.submitTask(task2));
        }

        futureList.forEach(f -> {
            try {
                System.out.println(Thread.currentThread().getName()+" # future => "+f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Completed Execution!");
        executor.shutDown();
    }
}
