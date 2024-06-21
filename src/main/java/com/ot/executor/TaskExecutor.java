package com.ot.executor;

import com.ot.model.Task;

import java.util.concurrent.Future;

/**
 * Submit new task to be queued and executed.
 *
 * @param task Task to be executed by the executor. Must not be null.
 * @return Future for the task asynchronous computation result.
 */
public interface TaskExecutor {

    <T> Future<T> submitTask(Task<T> task);
}
