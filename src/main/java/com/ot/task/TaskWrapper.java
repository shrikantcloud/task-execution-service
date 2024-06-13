package com.ot.task;


import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TaskWrapper implements Runnable {

    private final Task task;
    private final FutureTask<?> futureTask;

    public TaskWrapper(Task task) {
        this.task = task;
        this.futureTask = new FutureTask<>(task.taskAction());
    }

    public Task getTask() {
        return task;
    }

    public Future<?> getFuture() {
        return futureTask;
    }

    @Override
    public void run() {
        futureTask.run();
    }
}
