package com.yoanpetrov.scheduler;

import com.yoanpetrov.scheduler.service.TaskExecutor;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.tasks.TaskFactory;

import java.util.Scanner;

// this should not be a thread, we want this to execute in the current thread.
public class ConsoleTaskExecutorClient {

  private static final String STOP_MESSAGE = "STOP!";

  private final TaskExecutor taskExecutor;

  public ConsoleTaskExecutorClient(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public void start() {
    Scanner s = new Scanner(System.in);

    String line;
    boolean running = true;
    while (running) {
      Log.logger.info("Task type (standard, notifying, waiting): ");
      String type = s.nextLine().trim();

      if (type.equals(STOP_MESSAGE)) {
        running = false;
        continue;
      }

      Log.logger.info("Command: ");
      String command = s.nextLine().trim();

      if (command.equals(STOP_MESSAGE)) {
        running = false;
        continue;
      }

      try {
        Task t = getTask(type, command);
        taskExecutor.submitTask(t);
      } catch (IllegalArgumentException e) {
        Log.logger.error(e.getMessage());
      }
    }

    System.out.println("Waiting for task executor termination...");
    taskExecutor.shutdownAndAwaitTermination();
  }

  private Task getTask(String type, String command) {
    switch (type) {
      case "standard" -> {
        return TaskFactory.newStandardTask(command);
      }
      case "notifying" -> {
        return TaskFactory.newNotifyingTask(command);
      }
      case "waiting" -> {
        return TaskFactory.newWaitingTask(command, 3000);
      }
      default -> throw new IllegalArgumentException("Invalid task type!");
    }
  }
}
