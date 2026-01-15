package com.hytalelatam.hyannounces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hytalelatam.hyannounces.model.PluginConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manages plugin configuration loading and saving.
 */
public class ConfigManager {
    private final Path configPath;
    private final Gson gson;
    private PluginConfig config;

    public ConfigManager(Path dataFolder) {
        this.configPath = dataFolder.resolve("config.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Loads configuration from file (mods/HyBroadcaster/config.json). Creates
     * default if not exists.
     */
    public void load() {
        try {
            if (!Files.exists(configPath)) {
                // Copy default config from resources
                createDefaultConfig();
            }

            try (Reader reader = Files.newBufferedReader(configPath)) {
                config = gson.fromJson(reader, PluginConfig.class);
                if (config == null) {
                    HyAnnouncesPlugin.getInstance().getLogger().atWarning()
                            .log("Config file is empty, creating default configuration");
                    createDefaultConfig();
                    load(); // Reload
                }
            }

            HyAnnouncesPlugin.getInstance().getLogger().atInfo()
                    .log("Configuration loaded successfully with " +
                            config.getScheduledMessages().size() + " scheduled messages");

        } catch (IOException e) {
            HyAnnouncesPlugin.getInstance().getLogger().atSevere()
                    .log("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates default configuration file.
     */
    private void createDefaultConfig() throws IOException {
        // Ensure parent directory exists
        Files.createDirectories(configPath.getParent());

        // Copy from resources
        try (InputStream in = getClass().getResourceAsStream("/config.json")) {
            if (in != null) {
                Files.copy(in, configPath);
                HyAnnouncesPlugin.getInstance().getLogger().atInfo()
                        .log("Created default configuration file");
            } else {
                HyAnnouncesPlugin.getInstance().getLogger().atSevere()
                        .log("Could not find default config.json in resources");
            }
        }
    }

    /**
     * Reloads configuration from disk.
     */
    public void reload() {
        load();
    }

    /**
     * Gets the current configuration.
     */
    public PluginConfig getConfig() {
        return config;
    }
}
