package com.yoanpetrov.scheduler;

import com.yoanpetrov.scheduler.service.TaskExecutor;

public class Main {

    public static void main(String[] args) throws Exception {
        int threadPoolSize = 10; // default
        if (args.length >= 1) {
            threadPoolSize = Integer.parseInt(args[0]);
        }
        TaskExecutor taskExecutor = new TaskExecutor(threadPoolSize);
        ConsoleTaskExecutorClient consoleTaskExecutorClient = new ConsoleTaskExecutorClient(taskExecutor);

        consoleTaskExecutorClient.start();
    }
}
