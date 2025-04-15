package com.yoanpetrov.scheduler.service.tasks;

import java.util.List;

public record CommandLineTaskResult(int exitCode, List<String> outputs) {}
