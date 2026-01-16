package com.hytalelatam.hyannounces.util;

import com.hypixel.hytale.server.core.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for translating legacy color codes and hex codes into Hytale
 * Messages.
 */
public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    /**
     * Translates a string with legacy (&, §) and Hex (&#) codes into a Hytale
     * Message.
     * 
     * @param text The text to translate.
     * @return A formatted Message.
     */
    public static Message translate(String text) {
        if (text == null || text.isEmpty()) {
            return Message.empty();
        }

        // Handle Hex codes first: &#RRGGBB -> §#RRGGBB (internal placeholder)
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "§#" + matcher.group(1));
        }
        matcher.appendTail(sb);
        text = sb.toString();

        // Support both & and § for legacy codes
        text = text.replace("&", "§");

        if (!text.contains("§")) {
            return Message.raw(text);
        }

        List<Message> parts = new ArrayList<>();
        String[] split = text.split("§");

        // First part before any formatting code
        if (!split[0].isEmpty()) {
            parts.add(Message.raw(split[0]));
        }

        java.awt.Color currentColor = null;
        boolean isBold = false;
        boolean isItalic = false;

        for (int i = 1; i < split.length; i++) {
            String part = split[i];
            if (part.isEmpty())
                continue;

            if (part.startsWith("#") && part.length() >= 7) {
                // Hex color support
                String hex = part.substring(1, 7);
                try {
                    currentColor = new java.awt.Color(Integer.parseInt(hex, 16));
                    part = part.substring(7);
                } catch (NumberFormatException e) {
                    // Invalid hex, treat as normal text starting with #
                }
            } else {
                char code = part.charAt(0);
                part = part.substring(1);

                switch (Character.toLowerCase(code)) {
                    case '0':
                        currentColor = new java.awt.Color(0, 0, 0);
                        break;
                    case '1':
                        currentColor = new java.awt.Color(0, 0, 170);
                        break;
                    case '2':
                        currentColor = new java.awt.Color(0, 170, 0);
                        break;
                    case '3':
                        currentColor = new java.awt.Color(0, 170, 170);
                        break;
                    case '4':
                        currentColor = new java.awt.Color(170, 0, 0);
                        break;
                    case '5':
                        currentColor = new java.awt.Color(170, 0, 170);
                        break;
                    case '6':
                        currentColor = new java.awt.Color(255, 170, 0);
                        break;
                    case '7':
                        currentColor = new java.awt.Color(170, 170, 170);
                        break;
                    case '8':
                        currentColor = new java.awt.Color(85, 85, 85);
                        break;
                    case '9':
                        currentColor = new java.awt.Color(85, 85, 255);
                        break;
                    case 'a':
                        currentColor = new java.awt.Color(85, 255, 85);
                        break;
                    case 'b':
                        currentColor = new java.awt.Color(85, 255, 255);
                        break;
                    case 'c':
                        currentColor = new java.awt.Color(255, 85, 85);
                        break;
                    case 'd':
                        currentColor = new java.awt.Color(255, 85, 255);
                        break;
                    case 'e':
                        currentColor = new java.awt.Color(255, 255, 85);
                        break;
                    case 'f':
                        currentColor = new java.awt.Color(255, 255, 255);
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
                    default:
                        // Unknown code, add back the § tag and move on
                        part = "§" + code + part;
                        break;
                }
            }

            if (!part.isEmpty()) {
                Matcher urlMatcher = URL_PATTERN.matcher(part);
                int lastEnd = 0;

                while (urlMatcher.find()) {
                    String before = part.substring(lastEnd, urlMatcher.start());
                    String url = urlMatcher.group();

                    if (!before.isEmpty()) {
                        parts.add(createMessage(before, currentColor, isBold, isItalic));
                    }

                    parts.add(createMessage(url, currentColor, isBold, isItalic).link(url));
                    lastEnd = urlMatcher.end();
                }

                String remaining = part.substring(lastEnd);
                if (!remaining.isEmpty()) {
                    parts.add(createMessage(remaining, currentColor, isBold, isItalic));
                }
            }
        }

        if (parts.isEmpty())
            return Message.empty();

        // Use the first part as the root and insert the rest
        Message root = parts.get(0);
        for (int i = 1; i < parts.size(); i++) {
            root = root.insert(parts.get(i));
        }
        return root;
    }

    private static Message createMessage(String text, java.awt.Color color, boolean bold, boolean italic) {
        Message m = Message.raw(text);
        if (color != null)
            m = m.color(color);
        if (bold)
            m = m.bold(true);
        if (italic)
            m = m.italic(true);
        return m;
    }
}
