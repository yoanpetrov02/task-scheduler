package com.yoanpetrov.scheduler.service.workers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkerThreadPool {

  private final BlockingQueue<Worker> availableWorkers;
  private final ConcurrentHashMap<Long, Worker> busyWorkers;
  private final AtomicBoolean running;

  public WorkerThreadPool(int size) {
    this.availableWorkers =
        IntStream.range(0, size)
            .mapToObj(
                i -> {
                  Worker w = WorkerThreadFactory.newThread();
                  w.start();
                  return w;
                })
            .collect(Collectors.toCollection(LinkedBlockingQueue::new));
    this.busyWorkers = new ConcurrentHashMap<>();
    this.running = new AtomicBoolean(true);
  }

  public synchronized Worker retrieveWorkerForTask(long taskId) throws InterruptedException {
    if (!running.get()) {
      throw new IllegalStateException("Worker thread pool is not running!");
    }
    Worker w = availableWorkers.take();
    busyWorkers.put(taskId, w);
    return w;
  }

  public synchronized void returnWorker(long taskId, Worker w) throws InterruptedException {
    if (!running.get()) {
      throw new IllegalStateException("Worker thread pool is not running!");
    }
    if (!busyWorkers.containsKey(taskId)) {
      throw new IllegalStateException("Task is not present in the pool.");
    }
    if (!busyWorkers.contains(w)) {
      throw new IllegalStateException("Worker is not present in the pool.");
    }
    Worker freedWorker = busyWorkers.remove(taskId);
    availableWorkers.put(freedWorker);
  }

  public synchronized void shutdown() {
    running.set(false);
    busyWorkers.forEach((task, worker) -> worker.interrupt());
    availableWorkers.forEach(Thread::interrupt);
  }
}
