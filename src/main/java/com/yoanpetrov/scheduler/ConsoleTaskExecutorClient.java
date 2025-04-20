package com.yoanpetrov.scheduler;

import com.yoanpetrov.scheduler.service.TaskExecutor;
import com.yoanpetrov.scheduler.service.tasks.Task;
import com.yoanpetrov.scheduler.service.tasks.Tasks;

import java.util.Scanner;

// this should not be a thread, we want this to execute in the current thread.
public class ConsoleTaskExecutorClient {

    private static final String STOP_MESSAGE = "STOP!";

    private final TaskExecutor taskExecutor;

    public ConsoleTaskExecutorClient(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void start() {
        Scanner s = new Scanner(System.in);

        String line;
        boolean running = true;
        while (running) {
            System.out.print("Task type (standard, notifying, waiting): ");
            String type = s.nextLine().trim();
            System.out.print("Command: ");
            String command = s.nextLine().trim();

            if (type.equals(STOP_MESSAGE) || command.equals(STOP_MESSAGE)) {
                running = false;
                continue;
            }

            Task t = getTask(type, command);
            taskExecutor.submitTask(t);
        }
    }

    private Task getTask(String type, String command) {
        switch (type) {
            case "standard" -> {
                return Tasks.newStandardTask(command);
            }
            case "notifying" -> {
                return Tasks.newNotifyingTask(command);
            }
            case "waiting" -> {
                return Tasks.newWaitingTask(command, 3000);
            }
            default -> throw new IllegalArgumentException("Invalid task type!");
        }
    }
}
