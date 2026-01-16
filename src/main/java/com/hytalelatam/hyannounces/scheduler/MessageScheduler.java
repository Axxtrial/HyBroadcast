package com.hytalelatam.hyannounces.scheduler;

import com.hytalelatam.hyannounces.HyAnnouncesPlugin;
import com.hytalelatam.hyannounces.model.ScheduledMessage;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hytalelatam.hyannounces.util.ColorUtil;

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

    public MessageScheduler(HyAnnouncesPlugin plugin) {
        this.plugin = plugin;
        this.executor = Executors.newScheduledThreadPool(2);
        this.scheduledTasks = new ArrayList<>();
    }

    /**
     * Starts scheduling all configured messages.
     */
    public void start(List<ScheduledMessage> messages) {
        plugin.getLogger().atInfo().log("Starting message scheduler with " + messages.size() + " messages");

        for (ScheduledMessage msg : messages) {
            try {
                scheduleMessage(msg);
            } catch (Exception e) {
                plugin.getLogger().atSevere().log(
                        "Failed to schedule message with cron '" + msg.getSchedule() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Schedules a single message based on its cron expression.
     */
    private void scheduleMessage(ScheduledMessage msg) {
        CronScheduler cron = new CronScheduler(msg.getSchedule());

        // Capture the current executor instance to avoid rescheduling on a new one
        // after reload
        final ScheduledExecutorService currentExecutor = this.executor;

        // Schedule the recurring task
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    // Broadcast the message
                    broadcastMessage(msg);

                    // Calculate next execution and reschedule
                    boolean useUtc = plugin.getConfigManager().getConfig().isUseUtc();
                    long delay = cron.getDelayUntilNext(useUtc);

                    // Use the captured executor instance
                    // If this executor is shutdown (due to reload), this will throw
                    // RejectedExecutionException
                    // which stops the loop - exactly what we want.
                    currentExecutor.schedule(this, delay, TimeUnit.MILLISECONDS);

                    if (plugin.getConfigManager().getConfig().isDebugMode()) {
                        plugin.getLogger().atInfo().log(
                                "Message scheduled for next execution in " + (delay / 1000) + " seconds: "
                                        + msg.getMessage());
                    }
                } catch (RejectedExecutionException e) {
                    // Executor is shutdown, stop this task gracefully
                } catch (Exception e) {
                    plugin.getLogger().atSevere().log(
                            "Error executing scheduled message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // Schedule first execution
        boolean useUtc = plugin.getConfigManager().getConfig().isUseUtc();
        long initialDelay = cron.getDelayUntilNext(useUtc);
        ScheduledFuture<?> future = currentExecutor.schedule(task, initialDelay, TimeUnit.MILLISECONDS);
        scheduledTasks.add(future);

        plugin.getLogger().atInfo().log(
                "Scheduled message '" + msg.getMessage().substring(0, Math.min(30, msg.getMessage().length())) +
                        "...' with cron: " + msg.getSchedule() + " (next in " + (initialDelay / 1000) + "s)");
    }

    /**
     * Broadcasts a message to all online players.
     */
    private void broadcastMessage(ScheduledMessage msg) {
        Universe universe = Universe.get();
        int playerCount = universe.getPlayerCount();

        if (playerCount == 0) {
            if (plugin.getConfigManager().getConfig().isDebugMode()) {
                plugin.getLogger().atInfo().log("Skipping scheduled message (no players online)");
            }
            return;
        }

        universe.getPlayers().forEach(player -> {
            if (msg.isToast()) {
                // Toast notification (action bar message with custom prefix)
                String prefix = plugin.getConfigManager().getConfig().getToastPrefix();
                player.sendMessage(ColorUtil.translate(prefix + msg.getMessage()));
            } else {
                // Center screen announcement with custom title
                World world = universe.getWorld(player.getWorldUuid());
                if (world != null) {
                    // Use custom title if provided, otherwise use message as title
                    String title = (msg.getTitle() != null && !msg.getTitle().isEmpty())
                            ? msg.getTitle()
                            : msg.getMessage();

                    // Use message as subtitle if title is custom, otherwise use default
                    String subtitle = (msg.getTitle() != null && !msg.getTitle().isEmpty())
                            ? msg.getMessage()
                            : "Automatic Announcement";

                    EventTitleUtil.showEventTitleToPlayer(
                            player,
                            ColorUtil.translate(title),
                            ColorUtil.translate(subtitle),
                            true);
                }
            }
        });

        plugin.getLogger().atInfo().log(
                "Broadcasted scheduled message to " + playerCount + " player(s): " + msg.getMessage());
    }

    /**
     * Stops all scheduled tasks and shuts down the executor.
     */
    public void shutdown() {
        plugin.getLogger().atInfo().log("Shutting down message scheduler...");

        // Cancel all scheduled tasks
        for (ScheduledFuture<?> task : scheduledTasks) {
            task.cancel(false);
        }
        scheduledTasks.clear();

        // Immediate shutdown (no graceful wait)
        executor.shutdownNow();

        plugin.getLogger().atInfo().log("Message scheduler stopped");
    }

    /**
     * Restarts the scheduler with new messages.
     */
    public void restart(List<ScheduledMessage> messages) {
        // Shutdown old executor
        if (executor != null && !executor.isShutdown()) {
            shutdown();
        }

        // Create new executor
        executor = Executors.newScheduledThreadPool(2);
        scheduledTasks.clear();

        // Start with new messages
        start(messages);
    }
}
