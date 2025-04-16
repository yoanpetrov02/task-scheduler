package com.yoanpetrov.scheduler.service.tasks;

import java.util.concurrent.Callable;

public interface Task extends Callable<CommandLineTaskResult> {

    long id();
}
