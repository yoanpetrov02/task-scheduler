package com.yoanpetrov.scheduler.service;

public enum OperatingSystem {
  WINDOWS,
  LINUX;

  public static OperatingSystem getOperatingSystem() {
    String os = System.getProperty("os.name").toLowerCase().trim();

    if (os.startsWith("windows")) {
      return WINDOWS;
    }
    if (os.startsWith("linux")) {
      return LINUX;
    }
    throw new IllegalStateException("Unsupported operating system.");
  }
}
