package com.yoanpetrov.scheduler.service.workers;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.ObservableTaskExecutor;
import com.yoanpetrov.scheduler.service.TaskObserver;
import com.yoanpetrov.scheduler.service.results.TaskResult;
import com.yoanpetrov.scheduler.service.tasks.Task;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A worker thread, responsible for executing {@link Task} execution. Each worker maintains an
 * internal queue that can be used to pass tasks to it. While the thread is running, it will always
 * try to poll this queue and execute the given task, blocking if no task is available.
 */
public class Worker extends Thread implements ObservableTaskExecutor {

  private final SynchronousQueue<Task> taskQueue;
  private final AtomicBoolean running;
  private TaskObserver taskObserver;

  public Worker() {
    this.taskQueue = new SynchronousQueue<>();
    this.running = new AtomicBoolean();
  }

  @Override
  public void register(TaskObserver t) {
    taskObserver = t;
  }

  @Override
  public void run() {
    running.set(true);

    while (running.get() && !Thread.interrupted()) {
      Task t = null;
      try {
        t = taskQueue.take();
        Log.logger.debug("Executing task with id: {}", t.id());
        TaskResult result = t.call();
        taskObserver.notifyTaskFinished(t, this, result);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        terminate();
      } catch (Exception e) {
        if (t != null) {
          Log.logger.error("Error while executing task {}.", t.id(), e);
        } else {
          Log.logger.error("Error while executing task (task is null).");
        }
      }
    }
  }

  public void giveTask(Task t) throws InterruptedException {
    taskQueue.put(t);
  }

  private void terminate() {
    running.set(false);
  }
}
