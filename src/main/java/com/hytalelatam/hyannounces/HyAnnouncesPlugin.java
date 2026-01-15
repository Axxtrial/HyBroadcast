package com.hytalelatam.hyannounces;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hytalelatam.hyannounces.scheduler.MessageScheduler;
import javax.annotation.Nonnull;

/**
 * Main plugin class for HyAnnounces.
 */
public class HyAnnouncesPlugin extends JavaPlugin {

    private static HyAnnouncesPlugin instance;
    private ConfigManager configManager;
    private MessageScheduler messageScheduler;

    public HyAnnouncesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        // Initialize configuration
        // Create data folder path: mods/HyBroadcaster/
        java.nio.file.Path dataFolder = init.getFile().getParent().resolve("HyBroadcaster");
        configManager = new ConfigManager(dataFolder);
        configManager.load();

        getLogger().atInfo().log("HyAnnounces plugin loaded!");
    }

    @Override
    protected void start() {
        // Register commands
        getCommandRegistry().registerCommand(new AnnounceCommand());
        getCommandRegistry().registerCommand(new ReloadCommand());

        // Start message scheduler only if enabled
        if (configManager.getConfig().isEnableScheduledMessages()) {
            messageScheduler = new MessageScheduler(this);
            messageScheduler.start(configManager.getConfig().getScheduledMessages());
        } else {
            getLogger().atInfo().log("Scheduled messages are disabled in configuration");
        }

        getLogger().atInfo().log("HyAnnounces plugin enabled!");
    }

    @Override
    protected void shutdown() {
        // Stop scheduler
        if (messageScheduler != null) {
            messageScheduler.shutdown();
        }

        getLogger().atInfo().log("HyAnnounces plugin disabled!");
    }

    public static HyAnnouncesPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageScheduler getMessageScheduler() {
        return messageScheduler;
    }
}
