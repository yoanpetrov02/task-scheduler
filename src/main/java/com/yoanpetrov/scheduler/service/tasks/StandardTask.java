package com.yoanpetrov.scheduler.service.tasks;

import java.io.IOException;
import java.util.List;

class StandardTask implements Task {

  private final String[] commands;

  StandardTask(String[] commands) {
    this.commands = commands;
  }

  @Override
  public CommandLineTaskResult call() throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(commands);

    Process p = builder.start();
    p.waitFor();

    int exitCode = p.exitValue();
    List<String> outputs = StreamUtils.readLines(p.getInputStream());

    return new CommandLineTaskResult(exitCode, outputs);
  }
}
