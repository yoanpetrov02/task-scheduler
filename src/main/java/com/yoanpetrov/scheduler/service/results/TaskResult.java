package com.yoanpetrov.scheduler.service.results;

import java.util.List;

public record TaskResult(long taskId, int exitCode, List<String> inputs, List<String> outputs) {}
