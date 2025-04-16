package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.service.tasks.CommandLineTaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.Worker;

public interface TaskObserver {

  void notifyFinishedTask(Task t, Worker w, CommandLineTaskResult result);
}
