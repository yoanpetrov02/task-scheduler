package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.results.TaskResult;

import java.util.concurrent.Callable;

public interface Task extends Callable<TaskResult> {

    long id();
}
