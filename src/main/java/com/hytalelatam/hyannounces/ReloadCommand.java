package com.hytalelatam.hyannounces;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import javax.annotation.Nonnull;

/**
 * Command to reload plugin configuration.
 * Usage: /hyannounces reload
 */
public class ReloadCommand extends CommandBase {

    public ReloadCommand() {
        super("hyannounces", "HyAnnounces management commands", false);
        setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Permission check
        if (!context.sender().hasPermission("hybroadcaster.admin")
                && !context.sender().hasPermission("role.operator")) {
            context.sendMessage(
                    com.hypixel.hytale.server.core.Message.raw("You don't have permission to use this command."));
            return;
        }

        // Parse subcommand
        String input = context.getInputString();
        String[] parts = input.split(" ");

        if (parts.length < 2 || !parts[1].equalsIgnoreCase("reload")) {
            context.sendMessage(com.hypixel.hytale.server.core.Message.raw("Usage: /hyannounces reload"));
            return;
        }

        try {
            HyAnnouncesPlugin plugin = HyAnnouncesPlugin.getInstance();

            // Reload configuration
            plugin.getConfigManager().reload();

            // Restart scheduler only if enabled
            if (plugin.getConfigManager().getConfig().isEnableScheduledMessages()) {
                plugin.getMessageScheduler().restart(
                        plugin.getConfigManager().getConfig().getScheduledMessages());

                context.sendMessage(com.hypixel.hytale.server.core.Message.raw(
                        "Configuration reloaded successfully! " +
                                plugin.getConfigManager().getConfig().getScheduledMessages().size() +
                                " scheduled messages loaded."));
            } else {
                // Stop scheduler if it was running
                if (plugin.getMessageScheduler() != null) {
                    plugin.getMessageScheduler().shutdown();
                }

                context.sendMessage(com.hypixel.hytale.server.core.Message.raw(
                        "Configuration reloaded successfully! Scheduled messages are disabled."));
            }

            plugin.getLogger().atInfo().log("Configuration reloaded by admin");

        } catch (Exception e) {
            context.sendMessage(com.hypixel.hytale.server.core.Message.raw(
                    "Failed to reload configuration: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}
