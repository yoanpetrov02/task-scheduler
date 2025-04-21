package com.yoanpetrov.scheduler;

import com.yoanpetrov.scheduler.service.TaskExecutor;
import com.yoanpetrov.scheduler.service.results.FileResultStorage;
import com.yoanpetrov.scheduler.service.results.InMemoryResultStorage;
import com.yoanpetrov.scheduler.service.results.ResultStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    int threadPoolSize = 10; // default
    if (args.length >= 1) {
      threadPoolSize = Integer.parseInt(args[0]);
    }
    List<ResultStorage> resultStorages =
        List.of(new InMemoryResultStorage(), new FileResultStorage(Path.of("results.txt")));
    TaskExecutor taskExecutor = new TaskExecutor(threadPoolSize, resultStorages);
    ConsoleTaskExecutorClient consoleTaskExecutorClient =
        new ConsoleTaskExecutorClient(taskExecutor);

    consoleTaskExecutorClient.start();

    printInMemoryResults(resultStorages);
  }

  private static void printInMemoryResults(List<ResultStorage> resultStorages) {
    resultStorages.forEach(
        storage -> {
          if (storage instanceof InMemoryResultStorage s) {
            Log.logger.info("RESULTS: {}", s.getResults());
          }
        });
  }
}
