package com.hytalelatam.hyannounces.scheduler;

import com.hytalelatam.hyannounces.HyAnnouncesPlugin;
import com.hytalelatam.hyannounces.model.ScheduledMessage;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hytalelatam.hyannounces.util.ColorUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Manages scheduled message broadcasting.
 */
public class MessageScheduler {

    private final HyAnnouncesPlugin plugin;
    private ScheduledExecutorService executor;
    private final List<ScheduledFuture<?>> scheduledTasks;
    private volatile boolean active = true;

    private final String schedulerId;

    // Anti-Flood: Global State to prevent message bursts
    private static final Object LOCK = new Object();
    private static volatile long lastBroadcastTime = 0;

    public MessageScheduler(HyAnnouncesPlugin plugin) {
        this.plugin = plugin;
        this.executor = Executors.newScheduledThreadPool(2);
        this.scheduledTasks = new ArrayList<>();
        this.schedulerId = java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public void start(List<ScheduledMessage> messages) {
        plugin.getLogger().atInfo()
                .log("[ID: " + schedulerId + "] Starting message scheduler with " + messages.size() + " messages");

        java.util.Map<String, Integer> scheduleFrequency = new java.util.HashMap<>();

        for (ScheduledMessage msg : messages) {
            try {
                String schedule = msg.getSchedule();
                int collisionCount = scheduleFrequency.getOrDefault(schedule, 0);
                scheduleFrequency.put(schedule, collisionCount + 1);

                long collisionOffset = collisionCount * 2000L;

                if (collisionCount > 0) {
                    plugin.getLogger().atWarning().log(
                            "[ID: " + schedulerId + "] Auto-Correction: Collision for '" + schedule + "'. Offset: "
                                    + (collisionOffset / 1000) + "s");
                }

                scheduleMessage(msg, collisionOffset);
            } catch (Exception e) {
                plugin.getLogger().atSevere().log("[ID: " + schedulerId + "] Error scheduling: " + e.getMessage());
            }
        }
    }

    private void scheduleMessage(ScheduledMessage msg, long additionalOffset) {
        final ScheduledExecutorService currentExecutor = this.executor;
        boolean simpleMode = plugin.getConfigManager().getConfig().isSimpleMode();
        long initialDelay;
        if (simpleMode) {
            initialDelay = parseDuration(msg.getSchedule());
        } else {
            boolean useUtc = plugin.getConfigManager().getConfig().isUseUtc();
            CronScheduler cron = new CronScheduler(msg.getSchedule());
            initialDelay = cron.getDelayUntilNext(useUtc);
        }

        long initialDelayWithOffset = initialDelay + additionalOffset;
        long firstExpectedTime = System.currentTimeMillis() + initialDelayWithOffset;
        RecursiveTask task = new RecursiveTask(msg, currentExecutor, firstExpectedTime);

        ScheduledFuture<?> future = currentExecutor.schedule(task, initialDelayWithOffset, TimeUnit.MILLISECONDS);
        scheduledTasks.add(future);
    }

    private class RecursiveTask implements Runnable {
        private final ScheduledMessage msg;
        private final ScheduledExecutorService currentExecutor;
        private final CronScheduler cron;
        private final long simpleDuration;
        private final boolean simpleMode;
        private long expectedExecutionTime;

        public RecursiveTask(ScheduledMessage msg, ScheduledExecutorService executor, long expectedExecutionTime) {
            this.msg = msg;
            this.currentExecutor = executor;
            this.expectedExecutionTime = expectedExecutionTime;
            this.simpleMode = plugin.getConfigManager().getConfig().isSimpleMode();

            if (simpleMode) {
                this.simpleDuration = parseDuration(msg.getSchedule());
                this.cron = null;
            } else {
                this.simpleDuration = 0;
                this.cron = new CronScheduler(msg.getSchedule());
            }
        }

        @Override
        public void run() {
            if (!active)
                return;
            try {
                long now = System.currentTimeMillis();
                long interval = getInterval();
                if (interval <= 0)
                    interval = 1000;

                while (expectedExecutionTime <= now) {
                    expectedExecutionTime += interval;
                }

                // Async Broadcast
                CompletableFuture.runAsync(() -> broadcastMessage(msg));

                long nextTarget = expectedExecutionTime;
                if (nextTarget < now)
                    nextTarget = now;

                long currentInterval = getInterval();
                if (currentInterval < 1000)
                    currentInterval = 1000;

                nextTarget += currentInterval;
                while (nextTarget <= System.currentTimeMillis()) {
                    nextTarget += currentInterval;
                }

                this.expectedExecutionTime = nextTarget;
                long delay = nextTarget - System.currentTimeMillis();
                if (delay < 0)
                    delay = 0;

                if (!active)
                    return;
                currentExecutor.schedule(this, delay, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                plugin.getLogger().atSevere().log("[ID: " + schedulerId + "] Task error: " + e.getMessage());
            }
        }

        private long getInterval() {
            if (simpleMode)
                return simpleDuration;
            boolean useUtc = plugin.getConfigManager().getConfig().isUseUtc();
            LocalDateTime from = java.time.Instant.ofEpochMilli(expectedExecutionTime)
                    .atZone(useUtc ? java.time.ZoneOffset.UTC : java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime next = cron.getNextExecution(from);
            return next.atZone(useUtc ? java.time.ZoneOffset.UTC : java.time.ZoneId.systemDefault())
                    .toInstant().toEpochMilli() - expectedExecutionTime;
        }
    }

    private void broadcastMessage(ScheduledMessage msg) {
        synchronized (LOCK) {
            long now = System.currentTimeMillis();
            long timeSinceLast = now - lastBroadcastTime;
            if (timeSinceLast < 1500) {
                try {
                    Thread.sleep(1500 - timeSinceLast);
                } catch (InterruptedException e) {
                    return;
                }
            }
            lastBroadcastTime = System.currentTimeMillis();
        }

        Universe universe = Universe.get();
        int playerCount = universe.getPlayerCount();
        if (playerCount == 0)
            return;

        universe.getPlayers().forEach(player -> {
            World world = universe.getWorld(player.getWorldUuid());
            if (world != null) {
                world.execute(() -> {
                    // UI and Sound updates must be inside world.execute for thread safety
                    if (msg.isToast()) {
                        String prefix = plugin.getConfigManager().getConfig().getToastPrefix();
                        if (plugin.getConfigManager().getConfig().isShowTimestamp()) {
                            prefix = "[" + java.time.LocalTime.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + "] " + prefix;
                        }
                        player.sendMessage(ColorUtil.translate(prefix + msg.getMessage()));
                    } else {
                        String title = (msg.getTitle() != null && !msg.getTitle().isEmpty()) ? msg.getTitle()
                                : msg.getMessage();
                        String subtitle = (msg.getTitle() != null && !msg.getTitle().isEmpty()) ? msg.getMessage()
                                : "Automatic Announcement";
                        if (plugin.getConfigManager().getConfig().isShowTimestamp()) {
                            subtitle = "[" + java.time.LocalTime.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + "] " + subtitle;
                        }
                        EventTitleUtil.showEventTitleToPlayer(player, ColorUtil.translate(title),
                                ColorUtil.translate(subtitle), true);
                    }

                    // Sound playback (v2.0)
                    if (msg.getSound() != null && !msg.getSound().isEmpty()) {
                        com.hytalelatam.hyannounces.util.SoundUtil.playSound(player, msg.getSound());
                    }
                });
            }
        });
    }

    private long parseDuration(String durationStr) {
        durationStr = durationStr.toLowerCase().trim();
        long multiplier = 1000;
        if (durationStr.endsWith("s")) {
            durationStr = durationStr.substring(0, durationStr.length() - 1);
        } else if (durationStr.endsWith("m")) {
            multiplier = 60000;
            durationStr = durationStr.substring(0, durationStr.length() - 1);
        } else if (durationStr.endsWith("h")) {
            multiplier = 3600000;
            durationStr = durationStr.substring(0, durationStr.length() - 1);
        } else if (durationStr.endsWith("d")) {
            multiplier = 86400000;
            durationStr = durationStr.substring(0, durationStr.length() - 1);
        }
        return Long.parseLong(durationStr) * multiplier;
    }

    public void shutdown() {
        active = false;
        for (ScheduledFuture<?> task : scheduledTasks)
            task.cancel(false);
        scheduledTasks.clear();
        executor.shutdownNow();
    }
}
