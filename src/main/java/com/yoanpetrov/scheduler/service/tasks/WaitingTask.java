package com.yoanpetrov.scheduler.service.tasks;

import java.io.IOException;
import java.util.List;

class WaitingTask implements Task {

  private final long id;
  private final String[] commands;
  private final long waitMs;

  WaitingTask(long id, String[] commands, long waitMs) {
    this.id = id;
    this.commands = commands;
    this.waitMs = waitMs;
  }

  @Override
  public CommandLineTaskResult call() throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(commands);

    synchronized (this) {
      wait(waitMs);
    }

    Process p = builder.start();
    p.waitFor();

    int exitCode = p.exitValue();
    List<String> outputs = StreamUtils.readLines(p.getInputStream());

    return new CommandLineTaskResult(exitCode, outputs);
  }

  @Override
  public long id() {
    return id;
  }
}
