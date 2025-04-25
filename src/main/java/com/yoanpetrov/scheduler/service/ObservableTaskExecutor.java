package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.service.results.TaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.Worker;

/**
 * Represents an observable task executor. Allows for communication between the task executor and
 * the {@link TaskObserver}.
 */
public interface ObservableTaskExecutor {

  /**
   * Registers the given {@link TaskObserver} for notifications. Whenever this executor finishes a
   * task, it should call the observer's {@link TaskObserver#notifyTaskFinished(Task, Worker,
   * TaskResult)} method to notify it.
   *
   * @param t the task observer to register, not null.
   */
  void register(TaskObserver t);
}
