package com.yoanpetrov.scheduler.service;

import java.util.concurrent.ThreadFactory;

public class WorkerThreadFactory implements ThreadFactory {

  public static int numberOfThreadsCreated = 0;

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Worker();
    t.setName("Worker " + ++numberOfThreadsCreated);

    return t;
  }
}
