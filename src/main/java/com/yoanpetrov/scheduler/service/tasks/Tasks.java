package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.OperatingSystem;

import java.util.concurrent.atomic.AtomicLong;

public final class Tasks {

  private static final AtomicLong numberOfTasksCreated = new AtomicLong(0);

  public static Task newStandardTask(String command) {
    return new StandardTask(numberOfTasksCreated.addAndGet(1), generateCommandArray(command));
  }

  public static Task newWaitingTask(String command, long waitMs) {
    return new WaitingTask(numberOfTasksCreated.addAndGet(1), generateCommandArray(command), waitMs);
  }

  public static Task newNotifyingTask(String command) {
    return new NotifyingTask(numberOfTasksCreated.addAndGet(1), generateCommandArray(command));
  }

  private static String[] generateCommandArray(String command) {
    var os = OperatingSystem.getOperatingSystem();
    switch (os) {
      case LINUX -> {
        return new String[] {"sh", "-c", command};
      }
      case WINDOWS -> {
        return new String[] {"cmd.exe", "/c", command};
      }
      default -> {
        return new String[] {};
      }
    }
  }
}
