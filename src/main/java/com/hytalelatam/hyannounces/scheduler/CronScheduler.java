package com.hytalelatam.hyannounces.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Simple cron expression parser and scheduler for 5-field format.
 * Format: minute hour day month weekday
 * Supports: *, numbers, ranges (1-5), lists (1,3,5), intervals (*\/10)
 */
public class CronScheduler {

    private final String cronExpression;
    private final String minute;
    private final String hour;
    private final String day;
    private final String month;
    private final String weekday;

    public CronScheduler(String cronExpression) {
        this.cronExpression = cronExpression;
        String[] parts = cronExpression.trim().split("\\s+");

        if (parts.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid cron expression. Expected 5 fields (minute hour day month weekday), got: "
                            + cronExpression);
        }

        this.minute = parts[0];
        this.hour = parts[1];
        this.day = parts[2];
        this.month = parts[3];
        this.weekday = parts[4];
    }

    /**
     * Calculates the next execution time from now.
     */
    public LocalDateTime getNextExecution() {
        return getNextExecution(false);
    }

    /**
     * Calculates the next execution time based on UTC or local time.
     */
    public LocalDateTime getNextExecution(boolean useUtc) {
        LocalDateTime now = useUtc ? LocalDateTime.now(ZoneOffset.UTC) : LocalDateTime.now();
        return getNextExecution(now);
    }

    /**
     * Calculates the next execution time from a given time.
     */
    public LocalDateTime getNextExecution(LocalDateTime from) {
        LocalDateTime next = from.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);

        // Search for next valid time (max 1 year ahead to prevent infinite loops)
        LocalDateTime maxSearch = from.plusYears(1);

        while (next.isBefore(maxSearch)) {
            if (matches(next)) {
                return next;
            }
            next = next.plusMinutes(1);
        }

        throw new IllegalStateException("Could not find next execution time for cron: " + cronExpression);
    }

    /**
     * Checks if a given time matches the cron expression.
     */
    private boolean matches(LocalDateTime time) {
        return matchesField(minute, time.getMinute(), 0, 59)
                && matchesField(hour, time.getHour(), 0, 23)
                && matchesField(day, time.getDayOfMonth(), 1, 31)
                && matchesField(month, time.getMonthValue(), 1, 12)
                && matchesField(weekday, time.getDayOfWeek().getValue() % 7, 0, 6); // Convert to 0=Sunday
    }

    /**
     * Checks if a value matches a cron field.
     */
    private boolean matchesField(String field, int value, int min, int max) {
        // Wildcard
        if (field.equals("*")) {
            return true;
        }

        // Interval (e.g., */10)
        if (field.startsWith("*/")) {
            int interval = Integer.parseInt(field.substring(2));
            return value % interval == 0;
        }

        // List (e.g., 1,3,5)
        if (field.contains(",")) {
            String[] parts = field.split(",");
            for (String part : parts) {
                if (matchesField(part.trim(), value, min, max)) {
                    return true;
                }
            }
            return false;
        }

        // Range (e.g., 1-5)
        if (field.contains("-")) {
            String[] parts = field.split("-");
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());

            // Handle wrap-around ranges (e.g., 6-0 for Sat-Sun in weekday field)
            if (start > end) {
                // Value is in range if it's >= start OR <= end
                return value >= start || value <= end;
            }

            return value >= start && value <= end;
        }

        // Exact value
        try {
            return Integer.parseInt(field) == value;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets delay in milliseconds until next execution.
     */
    public long getDelayUntilNext() {
        return getDelayUntilNext(false);
    }

    /**
     * Gets delay in milliseconds until next execution.
     */
    public long getDelayUntilNext(boolean useUtc) {
        LocalDateTime now = useUtc ? LocalDateTime.now(ZoneOffset.UTC) : LocalDateTime.now();
        LocalDateTime next = getNextExecution(now);
        return ChronoUnit.MILLIS.between(now, next);
    }

    public String getCronExpression() {
        return cronExpression;
    }
}
