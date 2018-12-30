package com.artek;


public abstract class CustomTimerTask {
    private String taskName;
    private int times = 1;


    public CustomTimerTask(String taskName, int times) {
        this.taskName = taskName;
        this.times = times;
    }

    public void setTaskName(String taskName) { this.taskName = taskName; }

    public void setTimes(int times) { this.times = times; }

    public String getTaskName() { return taskName; }

    public int getTimes() { return times; }

    public abstract void execute();

}
