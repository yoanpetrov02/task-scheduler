package com.yoanpetrov.scheduler.service.results;

import java.util.List;
import java.util.Vector;

public class InMemoryResultStorage implements ResultStorage {

  private final List<TaskResult> results;

  public InMemoryResultStorage() {
    this.results = new Vector<>();
  }

  @Override
  public void store(TaskResult result) {
    results.add(result);
  }

  public List<TaskResult> getResults() {
    return results;
  }
}
