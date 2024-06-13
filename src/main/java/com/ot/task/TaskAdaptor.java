package com.ot.task;


import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TaskAdaptor implements Runnable {

    private final Task task;
    private final FutureTask<?> futureTask;

    public TaskAdaptor(Task task) {
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
