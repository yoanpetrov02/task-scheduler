package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.tasks.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {

  private final int threadPoolSize;
  private final ExecutorService workerThreadPool;
  private final Distributor distributorThread;

  public TaskExecutor(int threadPoolSize) {
    this.threadPoolSize = threadPoolSize;
    this.workerThreadPool = Executors.newFixedThreadPool(threadPoolSize, new WorkerThreadFactory());
    this.distributorThread = new Distributor(workerThreadPool);
    this.distributorThread.setDaemon(true);
    this.distributorThread.start();
    Log.logger.info("Started distributor thread");
  }

  public void submitTask(Task t) {

  }

  public void awaitTermination() throws InterruptedException {
      distributorThread.terminate();
      distributorThread.join();
  }

  public int getThreadPoolSize() {
      return threadPoolSize;
  }


}
