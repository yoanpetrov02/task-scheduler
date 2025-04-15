package com.yoanpetrov.scheduler.service;

import com.yoanpetrov.scheduler.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class Distributor extends Thread {

  private final ExecutorService workerThreadPool;
  private final AtomicBoolean running;

  public Distributor(ExecutorService workerThreadPool) {
    this.setName("Distributor");
    this.workerThreadPool = workerThreadPool;
    this.running = new AtomicBoolean();
  }

  @Override
  public void run() {
    running.set(true);

    while (running.get()) {
      synchronized (this) {
        try {
          wait(3000);
          Log.logger.debug("Waiting for new tasks...");
        } catch (InterruptedException e) {
          Log.logger.info("Distributor thread interrupted.");
        }
      }
    }
  }

  public void terminate() {
    running.set(false);
  }
}
