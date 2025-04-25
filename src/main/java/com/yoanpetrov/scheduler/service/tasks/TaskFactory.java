package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.OperatingSystem;

public final class TaskFactory {

  public static Task newStandardTask(String command, TaskIdGenerator idGenerator) {
    return new StandardTask(idGenerator.getNextId(), generateCommandArray(command));
  }

  public static Task newWaitingTask(String command, long waitMs, TaskIdGenerator idGenerator) {
    return new WaitingTask(idGenerator.getNextId(), generateCommandArray(command), waitMs);
  }

  public static Task newNotifyingTask(String command, TaskIdGenerator idGenerator) {
    return new NotifyingTask(idGenerator.getNextId(), generateCommandArray(command));
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
