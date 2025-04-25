package com.yoanpetrov.scheduler.service.workers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Manages {@link Worker} threads, encapsulating functionality for their lifecycle management. */
public class WorkerThreadPool {

  private final Set<Worker> workerSet;
  private final BlockingQueue<Worker> availableWorkers;
  private final AtomicBoolean running;

  public WorkerThreadPool(int size) {
    this.workerSet = Collections.synchronizedSet(new HashSet<>());
    this.availableWorkers =
        IntStream.range(0, size)
            .mapToObj(
                i -> {
                  Worker w = WorkerThreadFactory.newThread();
                  workerSet.add(w);
                  w.start();
                  return w;
                })
            .collect(Collectors.toCollection(LinkedBlockingQueue::new));
    this.running = new AtomicBoolean(true);
  }

  /**
   * Polls the pool of available workers to retrieve a worker thread. If no workers are available,
   * this method will block until one is returned to the pool. Returning a worker to the pool should
   * be done by calling {@link #returnWorker(Worker)}.
   *
   * @return the retrieved {@link Worker}.
   * @throws InterruptedException if the thread is interrupted.
   * @throws IllegalStateException if the pool is not running due to a prior {@link #shutdown()}
   *     call.
   */
  public synchronized Worker retrieveWorker() throws InterruptedException {
    if (!running.get()) {
      throw new IllegalStateException("Worker thread pool is not running!");
    }
    return availableWorkers.take();
  }

  /**
   * Returns the given worker to the pool of available worker threads.
   *
   * @param w the {@link Worker} to return to the pool.
   * @throws InterruptedException if the thread is interrupted.
   * @throws IllegalStateException if the pool is not running due to a prior {@link #shutdown()}
   *     call, or if the given worker is not managed by this pool.
   */
  public synchronized void returnWorker(Worker w) throws InterruptedException {
    if (!running.get()) {
      throw new IllegalStateException("Worker thread pool is not running!");
    }
    if (!workerSet.contains(w)) {
      throw new IllegalStateException(
          "Only workers that are managed by the pool can be returned to it!");
    }
    availableWorkers.put(w);
  }

  /**
   * Shuts down the thread pool, interrupting all worker threads that are managed by it. Note:
   * calling this method does not guarantee that all the workers will return to the pool. This means
   * that calling it will invalidate this object. Retrieving and returning workers after a shutdown
   * is an invalid operation.
   */
  public synchronized void shutdown() {
    running.set(false);
    workerSet.forEach(Thread::interrupt);
  }
}
