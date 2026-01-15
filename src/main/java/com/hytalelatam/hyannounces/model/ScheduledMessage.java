package com.hytalelatam.hyannounces.model;

/**
 * Represents a scheduled announcement message.
 */
public class ScheduledMessage {
    private String schedule; // Cron expression (5 fields: minute hour day month weekday)
    private boolean isToast; // Display as toast notification vs center screen
    private String title; // Title for center-screen announcements (optional)
    private String message; // Message content

    public ScheduledMessage() {
    }

    public ScheduledMessage(String schedule, boolean isToast, String title, String message) {
        this.schedule = schedule;
        this.isToast = isToast;
        this.title = title;
        this.message = message;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isToast() {
        return isToast;
    }

    public void setToast(boolean toast) {
        isToast = toast;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ScheduledMessage{" +
                "schedule='" + schedule + '\'' +
                ", isToast=" + isToast +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
