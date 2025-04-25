package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.service.results.TaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.Worker;

/**
 * Represent an observer that tracks the execution of a task. Task executors can notify the observer
 * when they finish executing a task.
 */
public interface TaskObserver {

  /**
   * Notifies the observer that the given task has finished. {@link ObservableTaskExecutor}
   * instances should call this method when they finish executing a task.
   *
   * @param t the finished task.
   * @param w the worker thread that executed the task.
   * @param result the result of the task.
   * @throws Exception if an error occurs while handling the finished task.
   */
  void notifyTaskFinished(Task t, Worker w, TaskResult result) throws Exception;
}
