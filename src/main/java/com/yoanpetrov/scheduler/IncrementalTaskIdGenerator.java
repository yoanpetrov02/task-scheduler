package com.yoanpetrov.scheduler;

import com.yoanpetrov.scheduler.service.tasks.TaskIdGenerator;

public class IncrementalTaskIdGenerator implements TaskIdGenerator {

    private long numberOfTasksCreated = 0;

    @Override
    public long getNextId() {
        return numberOfTasksCreated++;
    }
}
