package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.service.results.TaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.Worker;

public interface TaskObserver {

  void notifyFinishedTask(Task t, Worker w, TaskResult result) throws InterruptedException;
}
