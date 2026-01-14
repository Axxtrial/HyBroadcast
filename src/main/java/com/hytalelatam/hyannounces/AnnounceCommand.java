package com.hytalelatam.hyannounces;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
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
            context.sendMessage(translateLegacy("§cYou don't have permission to use this command."));
            return;
        }

        String input = context.getInputString();
        // Skip command name
        String[] parts = input.split(" ");
        if (parts.length <= 1) {
            context.sendMessage(translateLegacy("§cUsage: /announce <message> or /announce toast <message>"));
            return;
        }

        boolean isToast = parts[1].equalsIgnoreCase("toast");
        String messageStr;

        if (isToast) {
            if (parts.length < 3) {
                context.sendMessage(translateLegacy("§cPlease provide a message for the toast notification."));
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
                // Fallback action bar message
                player.sendMessage(translateLegacy("§6§l[ANNOUNCEMENT] §f" + finalMessage));
            } else {
                // Center Screen Announcement (Event Title)
                // Get the world the player is currently in
                World world = universe.getWorld(player.getWorldUuid());
                if (world != null) {
                    EventTitleUtil.showEventTitleToPlayer(
                            player,
                            translateLegacy("§6§l" + finalMessage), // Title
                            translateLegacy("§eAdmin Announcement"), // Subtitle
                            true // Animation
                    );
                }
            }
        });

        context.sendMessage(translateLegacy("§aAnnouncement sent successfully."));
    }

    private Message translateLegacy(String text) {
        // Support both & and § for colors
        text = text.replace("&", "§");
        if (!text.contains("§"))
            return Message.raw(text);

        java.util.List<Message> parts = new java.util.ArrayList<>();
        String[] split = text.split("§");

        // First part before any §
        if (!split[0].isEmpty()) {
            parts.add(Message.raw(split[0]));
        }

        String currentColor = null;
        boolean isBold = false;
        boolean isItalic = false;

        for (int i = 1; i < split.length; i++) {
            String part = split[i];
            if (part.isEmpty())
                continue;

            char code = part.charAt(0);
            String remaining = part.substring(1);

            switch (code) {
                case '0':
                    currentColor = "black";
                    break;
                case '1':
                    currentColor = "dark_blue";
                    break;
                case '2':
                    currentColor = "dark_green";
                    break;
                case '3':
                    currentColor = "dark_aqua";
                    break;
                case '4':
                    currentColor = "dark_red";
                    break;
                case '5':
                    currentColor = "dark_purple";
                    break;
                case '6':
                    currentColor = "gold";
                    break;
                case '7':
                    currentColor = "gray";
                    break;
                case '8':
                    currentColor = "dark_gray";
                    break;
                case '9':
                    currentColor = "blue";
                    break;
                case 'a':
                    currentColor = "green";
                    break;
                case 'b':
                    currentColor = "aqua";
                    break;
                case 'c':
                    currentColor = "red";
                    break;
                case 'd':
                    currentColor = "light_purple";
                    break;
                case 'e':
                    currentColor = "yellow";
                    break;
                case 'f':
                    currentColor = "white";
                    break;
                case 'l':
                    isBold = true;
                    break;
                case 'o':
                    isItalic = true;
                    break;
                case 'r':
                    currentColor = null;
                    isBold = false;
                    isItalic = false;
                    break;
            }

            if (!remaining.isEmpty()) {
                Message m = Message.raw(remaining);
                if (currentColor != null)
                    m.color(currentColor);
                if (isBold)
                    m.bold(true);
                if (isItalic)
                    m.italic(true);
                parts.add(m);
            }
        }

        if (parts.isEmpty())
            return Message.empty();
        if (parts.size() == 1)
            return parts.get(0);
        return Message.join(parts.toArray(new Message[0]));
    }
}
