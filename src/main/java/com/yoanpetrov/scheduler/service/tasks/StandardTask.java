package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.service.results.TaskResult;

import java.io.IOException;
import java.util.List;

class StandardTask implements Task {

  private final long id;
  private final String[] commands;

  StandardTask(long id, String[] commands) {
    this.id = id;
    this.commands = commands;
  }

  @Override
  public TaskResult call() throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(commands);

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
