package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.results.ResultStorage;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.WorkerThreadPool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskExecutor {

  private final WorkerThreadPool workerThreadPool;
  private final BlockingQueue<Task> taskQueue;
  private final Thread distributorThread;

  public TaskExecutor(int threadPoolSize, List<ResultStorage> resultStorages) {
    this.workerThreadPool = new WorkerThreadPool(threadPoolSize);
    this.taskQueue = new LinkedBlockingQueue<>();
    this.distributorThread =
        new Thread(new Distributor(workerThreadPool, taskQueue, resultStorages));
    this.distributorThread.setName("Distributor");
    this.distributorThread.setDaemon(true);
    this.distributorThread.start();
    Log.logger.info("Started distributor thread. Pool has {} threads.", threadPoolSize);
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
      distributorThread.interrupt();
      distributorThread.join(10000);
      workerThreadPool.shutdown();
    } catch (InterruptedException ignored) {
    }
  }
}
