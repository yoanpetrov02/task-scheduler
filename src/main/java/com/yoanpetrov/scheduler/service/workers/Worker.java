package com.yoanpetrov.scheduler.service.workers;

import com.yoanpetrov.scheduler.service.TaskObservable;
import com.yoanpetrov.scheduler.service.TaskObserver;
import com.yoanpetrov.scheduler.service.tasks.CommandLineTaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Worker extends Thread implements TaskObservable {

  private final SynchronousQueue<Task> taskQueue;
  private final AtomicLong lastTaskId;
  private final AtomicBoolean running;
  private TaskObserver taskObserver;

  public Worker() {
    this.taskQueue = new SynchronousQueue<>();
    this.lastTaskId = new AtomicLong(-1L);
    this.running = new AtomicBoolean();
  }

  @Override
  public void registerTaskObserver(TaskObserver t) {
    taskObserver = t;
  }

  @Override
  public void run() {
    running.set(true);

    while (running.get()) {
      try {
        Task t = taskQueue.take();
        lastTaskId.set(t.id());
        CommandLineTaskResult result = t.call();
        taskObserver.notifyFinishedTask(t, this, result);
      } catch (InterruptedException e) {

      } catch (Exception e) {

      }
    }
  }

  public void giveTask(Task t) throws InterruptedException {
    taskQueue.put(t);
  }

  public synchronized long getLastTaskId() {
    return lastTaskId.get();
  }

  public void terminate() {
    running.set(false);
  }
}
