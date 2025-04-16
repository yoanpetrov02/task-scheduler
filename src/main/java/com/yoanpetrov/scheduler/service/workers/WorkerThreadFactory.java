package com.yoanpetrov.scheduler.service.workers;

public class WorkerThreadFactory {

  public static int numberOfThreadsCreated = 0;

  public static Worker newThread() {
    Worker w = new Worker();
    w.setName("Worker " + ++numberOfThreadsCreated);

    return w;
  }
}
