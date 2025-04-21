package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.results.ResultStorage;
import com.yoanpetrov.scheduler.service.results.TaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.Worker;
import com.yoanpetrov.scheduler.service.workers.WorkerThreadPool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Distributor extends Thread implements TaskObserver {

  private final WorkerThreadPool workerThreadPool;
  private final BlockingQueue<Task> taskQueue;
  private final AtomicBoolean running;
  private final List<ResultStorage> resultStorages;

  public Distributor(
      WorkerThreadPool workerThreadPool,
      BlockingQueue<Task> taskQueue,
      List<ResultStorage> resultStorages) {
    this.setName("Distributor");
    this.workerThreadPool = workerThreadPool;
    this.taskQueue = taskQueue;
    this.running = new AtomicBoolean();
    this.resultStorages = resultStorages;
  }

  @Override
  public void run() {
    running.set(true);

    while (running.get()) {
      try {
        Task t = taskQueue.take();
        Log.logger.debug("Scheduling task with id: {}", t.id());
        Worker w = workerThreadPool.retrieveWorkerForTask(t.id());
        w.registerTaskObserver(this);
        w.giveTask(t);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        terminate();
      }
    }
  }

  @Override
  public void notifyFinishedTask(Task t, Worker w, TaskResult result) throws InterruptedException {
    Log.logger.debug("Task with id: {} has finished execution.", t.id());
    workerThreadPool.returnWorker(t.id(), w);
    storeResult(result);
  }

  public void terminate() {
    running.set(false);
  }

  private void storeResult(TaskResult result) {
    resultStorages.forEach(storage -> storage.store(result));
  }
}
