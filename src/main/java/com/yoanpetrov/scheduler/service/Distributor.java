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

/**
 * A task distributor, responsible for handling tasks over to worker threads that execute them, and
 * recording/logging the results.
 */
public class Distributor implements Runnable, TaskObserver {

  private final WorkerThreadPool workerThreadPool;
  private final BlockingQueue<Task> taskQueue;
  private final AtomicBoolean running;
  private final List<ResultStorage> resultStorages;

  public Distributor(
      WorkerThreadPool workerThreadPool,
      BlockingQueue<Task> taskQueue,
      List<ResultStorage> resultStorages) {
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
        Worker w = workerThreadPool.retrieveWorker();
        w.register(this);
        w.giveTask(t);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        terminate();
      } catch (IllegalStateException e) {
        Log.logger.warn("Couldn't retrieve worker for task: ", e);
      }
    }
  }

  @Override
  public void notifyTaskFinished(Task t, Worker w, TaskResult result) throws InterruptedException {
    Log.logger.debug("Task with id: {} has finished execution.", t.id());
    try {
      workerThreadPool.returnWorker(w);
    } catch (IllegalStateException e) {
      Log.logger.warn("Couldn't return thread \"{}\" to thread pool: ", w.getName(), e);
    }
    storeResult(result);
  }

  private void terminate() {
    running.set(false);
  }

  private void storeResult(TaskResult result) {
    resultStorages.forEach(storage -> storage.store(result));
  }
}
