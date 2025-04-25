package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.results.TaskResult;

import java.util.concurrent.Callable;

/** Represents a task that returns a {@link TaskResult}. */
public interface Task extends Callable<TaskResult> {

  /** Returns the id of the task. */
  long id();
}
