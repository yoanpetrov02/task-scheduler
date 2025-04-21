package com.yoanpetrov.scheduler.service.tasks;

import com.yoanpetrov.scheduler.Log;
import com.yoanpetrov.scheduler.service.results.TaskResult;

import java.util.List;

class NotifyingTask implements Task {

  private final long id;
  private final String[] commands;

  NotifyingTask(long id, String[] commands) {
    this.id = id;
    this.commands = commands;
  }

  @Override
  public TaskResult call() throws Exception {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(commands);

    Log.logger.info("START");

    Process p = builder.start();
    p.waitFor();

    Log.logger.info("END");

    int exitCode = p.exitValue();
    List<String> outputs = StreamUtils.readLines(p.getInputStream());

    return new TaskResult(id, exitCode, List.of(commands), outputs);
  }

  @Override
  public long id() {
    return id;
  }
}
