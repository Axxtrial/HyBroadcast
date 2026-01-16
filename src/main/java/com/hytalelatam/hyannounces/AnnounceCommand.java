package com.hytalelatam.hyannounces;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hytalelatam.hyannounces.util.ColorUtil;
import javax.annotation.Nonnull;

/**
 * Command to display announcements to all players.
 * Usage: /announce <message> (Center Screen)
 * Usage: /announce toast <message> (Toast Notification)
 */
public class AnnounceCommand extends CommandBase {

    public AnnounceCommand() {
        // Command name, description, requires OP (false to avoid mandatory --confirm
        // flag in some versions)
        super("announce", "Shows a professional announcement to all players", false);
        setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Permission check (since we disabled the OP flag in the constructor to avoid
        // the --confirm requirement)
        if (!context.sender().hasPermission("hybroadcaster.admin")
                && !context.sender().hasPermission("role.operator")) {
            context.sendMessage(ColorUtil.translate("You don't have permission to use this command."));
            return;
        }

        String input = context.getInputString();
        // Skip command name
        String[] parts = input.split(" ");
        if (parts.length <= 1) {
            context.sendMessage(ColorUtil.translate("Usage: /announce <message> or /announce toast <message>"));
            return;
        }

        boolean isToast = parts[1].equalsIgnoreCase("toast");
        String messageStr;

        if (isToast) {
            if (parts.length < 3) {
                context.sendMessage(ColorUtil.translate("Please provide a message for the toast notification."));
                return;
            }
            messageStr = String.join(" ", java.util.Arrays.copyOfRange(parts, 2, parts.length));
        } else {
            messageStr = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }

        final String finalMessage = messageStr;

        // Broadcast to all players using Universe global
        Universe universe = Universe.get();
        universe.getPlayers().forEach(player -> {
            if (isToast) {
                // Use prefix from configuration
                String prefix = HyAnnouncesPlugin.getInstance().getConfigManager().getConfig().getToastPrefix();
                player.sendMessage(ColorUtil.translate(prefix + finalMessage));
            } else {
                // Center Screen Announcement (Event Title)
                // Get the world the player is currently in
                World world = universe.getWorld(player.getWorldUuid());
                if (world != null) {
                    EventTitleUtil.showEventTitleToPlayer(
                            player,
                            ColorUtil.translate(finalMessage), // Title
                            ColorUtil.translate("Admin Announcement"), // Subtitle
                            true // Animation
                    );
                }
            }
        });

        context.sendMessage(ColorUtil.translate("Announcement sent successfully."));
    }

}
