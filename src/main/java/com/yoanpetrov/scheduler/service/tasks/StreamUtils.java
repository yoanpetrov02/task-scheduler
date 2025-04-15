package com.yoanpetrov.scheduler.service.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

final class StreamUtils {

  private StreamUtils() {}

  static List<String> readLines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream)).lines().map(String::trim).toList();
  }
}
