package com.yoanpetrov.scheduler.service.results;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;

public class FileResultStorage implements ResultStorage {

  private static final String HEADER_TEMPLATE = "[%s] task id: %d, exit code: %d%ncommand: %s";
  private static final String OUTPUTS_TEMPLATE = "outputs: %s%n";

  private final PrintStream outputStream;

  public FileResultStorage(Path destinationFile) throws IOException {
    outputStream = new PrintStream(new FileOutputStream(destinationFile.toFile(), true));
  }

  @Override
  public synchronized void store(TaskResult result) {
    outputStream.println(getHeader(result));
    outputStream.println(getOutputs(result));
  }

  private String getHeader(TaskResult result) {
    return HEADER_TEMPLATE.formatted(
        LocalDate.now(), result.taskId(), result.exitCode(), result.inputs());
  }

  private String getOutputs(TaskResult result) {
    return OUTPUTS_TEMPLATE.formatted(result.outputs());
  }
}
