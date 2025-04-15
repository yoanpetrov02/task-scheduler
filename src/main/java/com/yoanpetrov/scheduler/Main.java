package com.yoanpetrov.scheduler;

import com.yoanpetrov.scheduler.service.TaskExecutor;

public class Main {

    public static void main(String[] args) throws Exception {
        TaskExecutor taskExecutor = new TaskExecutor(10);

        Thread.sleep(10000);

        taskExecutor.awaitTermination();
    }
}
