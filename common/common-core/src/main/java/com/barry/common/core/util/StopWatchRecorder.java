package com.barry.common.core.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 停表记录器.
 *
 * @author barry chen
 * @date 2020/9/21 5:37 下午
 */
public class StopWatchRecorder {
    private final StringBuilder content = new StringBuilder();
    private LocalDateTime startTime;
    private LocalDateTime stopTime;

    public StopWatchRecorder() {
    }

    public void start(String words) {
        startTime = LocalDateTime.now();
        this.record(startTime, words);
    }

    public void split(String words) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        LocalDateTime now = LocalDateTime.now();
        this.record(now, ChronoUnit.MILLIS.between(startTime, now) + "ms  " + words);
    }

    public String stop(String words) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        stopTime = LocalDateTime.now();
        this.record(stopTime, ChronoUnit.MILLIS.between(startTime, stopTime) + "ms=" + ChronoUnit.SECONDS.between(startTime, stopTime) + "s  " + words);
        return this.getContent().toString();
    }

    public String stopAndOutput(String words) {
        stop(words);
        return content.toString();
    }

    private void record(LocalDateTime time, String words) {
        content.append(time.toString()).append("  ").append(words).append("\n");
    }

    public StringBuilder getContent() {
        return content;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

}
