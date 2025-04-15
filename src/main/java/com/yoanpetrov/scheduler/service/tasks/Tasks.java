package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.OperatingSystem;

public final class Tasks {

  public static Task newStandardTask(String command) {
    return new StandardTask(generateCommandArray(command));
  }

  public static Task newWaitingTask(String command, long waitMs) {
    return new WaitingTask(generateCommandArray(command), waitMs);
  }

  public static Task newNotifyingTask(String command) {
    return new NotifyingTask(generateCommandArray(command));
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
