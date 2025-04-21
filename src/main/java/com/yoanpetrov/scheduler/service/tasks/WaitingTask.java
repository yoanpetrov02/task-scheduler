package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.results.TaskResult;

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
  public TaskResult call() throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(commands);

    synchronized (this) {
      wait(waitMs);
    }

    Process p = builder.start();
    p.waitFor();

    int exitCode = p.exitValue();
    List<String> outputs = StreamUtils.readLines(p.getInputStream());

    return new TaskResult(id, exitCode, List.of(commands), outputs);
  }

  @Override
  public long id() {
    return id;
  }
}
