package com.hytalelatam.hyannounces;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hytalelatam.hyannounces.util.ColorUtil;
import javax.annotation.Nonnull;

/**
 * Command to display announcements to all players.
 * Usage: /announce <message> [--sound <name>]
 * Tip: Put --sound at the end of the message to avoid parsing conflicts.
 */
public class AnnounceCommand extends CommandBase {

    private final OptionalArg<String> soundArg = this.withOptionalArg("sound", "Optional sound to play",
            ArgTypes.STRING);

    public AnnounceCommand() {
        // Command name, description, requires OP (false to avoid mandatory --confirm
        // flag in some versions)
        super("announce", "Shows a professional announcement to all players", false);
        setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        if (!context.sender().hasPermission("hybroadcaster.admin")
                && !context.sender().hasPermission("role.operator")) {
            context.sendMessage(ColorUtil.translate("You don't have permission to use this command."));
            return;
        }

        String rawInput = context.getInputString();
        String[] parts = rawInput.split("\\s+");
        if (parts.length <= 1) {
            context.sendMessage(ColorUtil.translate("Usage: /announce <message> [--sound <name>]"));
            context.sendMessage(ColorUtil.translate("Tip: Put --sound at the end of the message for best results."));
            return;
        }

        // Retrieve native optional argument
        String soundName = soundArg.get(context);

        // Parse everything manually to build the final message
        java.util.List<String> messageParts = new java.util.ArrayList<>();
        boolean isToast = false;
        boolean skipNext = false;

        for (int i = 1; i < parts.length; i++) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
            String part = parts[i];

            if (part.equalsIgnoreCase("toast")) {
                isToast = true;
                continue;
            }

            if (part.equalsIgnoreCase("--sound") || part.equalsIgnoreCase("-sound")) {
                // If it's the flag, just skip it and its value
                skipNext = true;
                continue;
            }

            messageParts.add(part);
        }

        if (messageParts.isEmpty()) {
            context.sendMessage(ColorUtil.translate("Please provide a message."));
            return;
        }

        final boolean finalIsToast = isToast;
        final String finalMessage = String.join(" ", messageParts);
        final String finalSound = soundName;

        Universe universe = Universe.get();
        universe.getPlayers().forEach(player -> {
            World world = universe.getWorld(player.getWorldUuid());
            if (world != null) {
                world.execute(() -> {
                    // Play sound if provided
                    if (finalSound != null) {
                        com.hytalelatam.hyannounces.util.SoundUtil.playSound(player, finalSound);
                    }

                    if (finalIsToast) {
                        String prefix = HyAnnouncesPlugin.getInstance().getConfigManager().getConfig().getToastPrefix();
                        player.sendMessage(ColorUtil.translate(prefix + finalMessage));
                    } else {
                        EventTitleUtil.showEventTitleToPlayer(
                                player,
                                ColorUtil.translate(finalMessage),
                                ColorUtil.translate("Admin Announcement"),
                                true);
                    }
                });
            }
        });

        context.sendMessage(ColorUtil.translate("Announcement sent successfully."));
    }

}
