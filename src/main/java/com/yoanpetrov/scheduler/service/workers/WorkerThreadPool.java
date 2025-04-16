package com.yoanpetrov.scheduler.service.workers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkerThreadPool {

  private final BlockingQueue<Worker> availableWorkers;
  private final ConcurrentHashMap<Long, Worker> busyWorkers;

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
  }

  public synchronized Worker retrieveWorkerForTask(long taskId) throws InterruptedException {
    Worker w = availableWorkers.take();
    busyWorkers.put(taskId, w);
    return w;
  }

  public synchronized void returnWorker(long taskId, Worker w) throws InterruptedException {
    if (!busyWorkers.containsKey(taskId)) {
      throw new IllegalStateException("Task is not present in the pool.");
    }
    if (!busyWorkers.contains(w)) {
      throw new IllegalStateException("Worker is not present in the pool.");
    }
    Worker freedWorker = busyWorkers.remove(taskId);
    availableWorkers.put(freedWorker);
  }
}
