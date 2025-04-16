package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.Log;

import java.util.List;

class NotifyingTask implements Task {

  private final long id;
  private final String[] commands;

  NotifyingTask(long id, String[] commands) {
    this.id = id;
    this.commands = commands;
  }

  @Override
  public CommandLineTaskResult call() throws Exception {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(commands);

    Log.logger.info("START");

    Process p = builder.start();
    p.waitFor();

    Log.logger.info("END");

    int exitCode = p.exitValue();
    List<String> outputs = StreamUtils.readLines(p.getInputStream());

    return new CommandLineTaskResult(exitCode, outputs);
  }

  @Override
  public long id() {
    return id;
  }
}
