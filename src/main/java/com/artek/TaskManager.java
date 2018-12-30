package com.artek;

import org.telegram.telegrambots.meta.logging.BotLogger;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private static final String LOGTAG = "TaskManager";
    private static volatile TaskManager instance;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public TaskManager() {}

    public static TaskManager getInstance() {
        final TaskManager currentInstance;
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
                currentInstance = instance;
            }
        }

        else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    public void startExecutionEveryDayAt(CustomTimerTask task, int targetHour, int targetMin, int targetSec) {
        BotLogger.warn(LOGTAG, "Starting execution of timer");

        final Runnable taskWrapper = () -> {
            try {
                task.execute();
                startExecutionEveryDayAt(task, targetHour, targetMin, targetSec);
            }

            catch (Exception e) {
                BotLogger.severe(LOGTAG, "Error in TaskManager");
            }
        };

        if (task.getTimes() != 0) {
            final long delay = computeDelay(targetHour, targetMin, targetSec);
            executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
        }
    }

    private long computeDelay(int targetHour, int targetMin, int targetSec) {
        final LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime localNext = localNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        while (localNow.compareTo(localNext.minusSeconds(1)) > 0) {
            localNext.plusDays(1);
        }

        final Duration duration = Duration.between(localNow, localNext);
        return duration.getSeconds();
    }
}
