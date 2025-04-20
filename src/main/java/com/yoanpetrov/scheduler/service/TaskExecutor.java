package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.WorkerThreadPool;

import java.util.concurrent.*;

public class TaskExecutor {

  private final WorkerThreadPool workerThreadPool;
  private final BlockingQueue<Task> taskQueue;
  private final Distributor distributorThread;

  public TaskExecutor(int threadPoolSize) {
    this.workerThreadPool = new WorkerThreadPool(threadPoolSize);
    this.taskQueue = new LinkedBlockingQueue<>();
    this.distributorThread = new Distributor(workerThreadPool, taskQueue);
    this.distributorThread.setDaemon(true);
    this.distributorThread.start();
    Log.logger.info("Started distributor thread");
  }

  public void submitTask(Task t) {
    try {
      Log.logger.info("Submitting task with id {}", t.id());
      taskQueue.put(t);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      shutdownAndAwaitTermination();
    }
  }

  public void shutdownAndAwaitTermination() {
    try {
      distributorThread.terminate();
      distributorThread.join(10000);
      workerThreadPool.shutdown();
    } catch (InterruptedException ignored) {}
  }
}
