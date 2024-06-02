package com.linsheng.FATJS.cron4j;

import it.sauronsoftware.cron4j.Scheduler;

public class CronTask {

    private Scheduler scheduler;

    public CronTask() {
        scheduler = new Scheduler();
    }

    public void scheduleTask(String cronExpression, Runnable task) {
        scheduler.schedule(cronExpression, task);
    }

    public void start() {
        scheduler.start();
    }

    public void stop() {
        scheduler.stop();
    }

    public void clearAllTasks() {
        if (scheduler.isStarted()) {
            scheduler.stop();
        }
        scheduler = new Scheduler(); // 重置调度器
    }

}