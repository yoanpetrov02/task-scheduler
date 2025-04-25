package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.results.ResultStorage;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.workers.WorkerThreadPool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Task execution manager. Manages a distributor thread and a pool of worker threads. Submitting
 * tasks to the executor passes them to a shared queue, which gets polled by the distributor, which
 * in term distributes the tasks between the workers.
 */
public class TaskExecutor {

  private final WorkerThreadPool workerThreadPool;
  private final BlockingQueue<Task> taskQueue;
  private final Thread distributorThread;
  private final AtomicBoolean isShutdown;

  public TaskExecutor(int threadPoolSize, List<ResultStorage> resultStorages) {
    this.workerThreadPool = new WorkerThreadPool(threadPoolSize);
    this.taskQueue = new LinkedBlockingQueue<>();
    this.distributorThread =
        new Thread(new Distributor(workerThreadPool, taskQueue, resultStorages));
    this.distributorThread.setName("Distributor");
    this.distributorThread.setDaemon(true);
    this.distributorThread.start();
    this.isShutdown = new AtomicBoolean();
    Log.logger.info("Started distributor thread. Pool has {} threads.", threadPoolSize);
  }

  /**
   * Submits a {@link Task} for execution. The task will be put in the task queue, waiting for the
   * distributor to pick it up.
   *
   * @param t the task to submit.
   * @throws IllegalStateException if the task executor has been shut down due to a prior {@link
   *     #shutdownAndAwaitTermination()} call.
   */
  public void submitTask(Task t) throws IllegalStateException {
    if (isShutdown.get()) {
      throw new IllegalStateException("Task executor has been shut down!");
    }
    try {
      Log.logger.info("Submitting task with id {}", t.id());
      taskQueue.put(t);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      shutdownAndAwaitTermination();
    }
  }

  /**
   * Shuts down the task executor and awaits the termination of the distributor thread. The worker
   * thread pool is shutdown, setting the interrupted status of all worker threads. Note: calling
   * this method invalidates the task executor. Further {@link #submitTask(Task)} calls will throw
   * an {@link IllegalStateException} as they will be considered an invalid operation.
   */
  public void shutdownAndAwaitTermination() {
    isShutdown.set(true);
    try {
      distributorThread.interrupt();
      distributorThread.join(10000);
      workerThreadPool.shutdown();
    } catch (InterruptedException ignored) {
    }
  }
}
