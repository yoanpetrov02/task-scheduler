package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.service.tasks.CommandLineTaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.Worker;
import com.yoanpetrov.scheduler.service.workers.WorkerThreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Distributor extends Thread implements TaskObserver {

  private final WorkerThreadPool workerThreadPool;
  private final BlockingQueue<Task> taskQueue;
  private final AtomicBoolean running;

  public Distributor(WorkerThreadPool workerThreadPool, BlockingQueue<Task> taskQueue) {
    this.setName("Distributor");
    this.workerThreadPool = workerThreadPool;
    this.taskQueue = taskQueue;
    this.running = new AtomicBoolean();
  }

  @Override
  public void run() {
    running.set(true);

    while (running.get()) {
      try {
        Task t = taskQueue.take();
        Worker w = workerThreadPool.retrieveWorkerForTask(t.id());
        w.giveTask(t);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        terminate();
      }
    }
  }

  // this method will be called by Worker threads, which means that they will hang while waiting to
  // return to the pool.
  @Override
  public void notifyFinishedTask(Task t, Worker w, CommandLineTaskResult result)
      throws InterruptedException {
    workerThreadPool.returnWorker(t.id(), w);
    // todo save the result. Implement a ResultStorage interface with multiple implementations that can save in different stores. Store can be in-memory (object) or file.
  }

  public void terminate() {
    running.set(false);
  }
}
