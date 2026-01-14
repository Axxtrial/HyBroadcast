package com.hytalelatam.hyannounces;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

/**
 * Main plugin class for HyAnnounces.
 */
public class HyAnnouncesPlugin extends JavaPlugin {

    private static HyAnnouncesPlugin instance;

    public HyAnnouncesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().atInfo().log("HyAnnounces plugin cargado!");
    }

    @Override
    protected void start() {
        // Register the announcement command
        getCommandRegistry().registerCommand(new AnnounceCommand());

        getLogger().atInfo().log("HyAnnounces plugin activado!");
    }

    @Override
    protected void shutdown() {
        getLogger().atInfo().log("HyAnnounces plugin desactivado!");
    }

    public static HyAnnouncesPlugin getInstance() {
        return instance;
    }
}
