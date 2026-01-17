# HyBroadcaster

HyBroadcaster is a professional announcement mod for Hytale servers. It allows administrators to send global messages using center screen titles or toast notifications, with optional cron-based automation.

## Features

*   **New (v1.4.1): Timestamps**: Optionally show `[HH:mm]` in your announcements.
*   **New (v1.4.1): Configurable Lag Protection**: Smart scheduling ensures messages don't pile up. Now toggleable!
*   **Simple Mode (v1.4.0)**: Start quickly using simple durations (e.g., `"10s"`, `"5m"`) instead of checking cron docs.
*   **Manual & Scheduled**: Send instant or automated announcements.
*   **Dual Display**: Choose between large center-screen titles or chat toasts.
*   **Rich Text & Colors**: Full support for Legacy codes (`&f`) and HEX formats (`&#FFFFFF`).
*   **Automation**: Set up repetitive messages using standard cron `0 12 * * *` or simple durations.
*   **Timezone Control**: Support for UTC or local server time scheduling.
*   **Easy Management**: Hot reload configuration and global toggle support.

## Display Modes

### Center Screen Title
Perfect for important alerts and events. Displays a high-visibility banner in the middle of the screen. 
![Center Title Example](https://media.discordapp.net/attachments/1444178568354463850/1460862884778213441/image.png?ex=696a7055&is=69691ed5&hm=11dffe293d8d4fa2a509abe19efb57750bac6b0af8308f8eb8fb0f2bf5ce1748&=&format=webp&quality=lossless&width=1620&height=856)

### Chat Toast (Emblem)
Ideal for automated tips or less intrusive server information. 
![Toast Example](https://media.discordapp.net/attachments/1444178568354463850/1460863361397821615/image.png?ex=696a70c6&is=69691f46&hm=3fff43257da00fd95e907c591dfadf252ebc3a52a9d5812b635977c3bc08ac67&=&format=webp&quality=lossless&width=1625&height=856)

## Color & Formatting Support

Make your announcements stand out with our advanced styling engine:

*   **Legacy Codes**: Use the classic `&0` - `&f` plus formatting like `&l` (Bold).
*   **Hex Support**: Professional-grade colors using `&#RRGGBB`.

**Example Hex Formatting:** `&#a29bfe&lSTORE: &fGet your &#fdcb6e&lV.I.P Rank &fnow at &b&owww.hytale.com`

![Hex Example](https://media.discordapp.net/attachments/1444178568354463850/1461499614938267710/image.png?ex=696ac715&is=69697595&hm=e0bff063e1301db7d36058a1251858db5ba3427ac97ed9fc105b937f2ef4cfa1&=&format=webp&quality=lossless)

## Commands

*   `/announce <message>`: Displays a large title in the center of the screen.
*   `/announce toast <message>`: Sends a global announcement to the chat.
*   `/hyannounces reload`: Refresh the configuration and restart the scheduler instantly.

## [Web Configuration Tool & Live Editor](https://axxtrial.github.io/HyBroadcast/)

Don't waste time guessing! Use our **Live Editor** to:
*   Preview colors (Hex/Legacy) in real-time.
*   **New:** Generate configuration for both **Simple Mode** and **Cron Mode**.
*   It's **completely free and ad-free**.

## 📢 Discord Support

Need help? Found a bug? Join our official community: 👉 **[https://discord.gg/rJ3zd8MNHG](https://discord.gg/rJ3zd8MNHG)**

***

## Installation

1.  Place the `HyBroadcaster.jar` into your server's `mods/` folder.
2.  Restart the server.
3.  Edit `mods/HyBroadcaster/config.json` to customize your messages.
4.  Use `/hyannounces reload` to apply changes without restarting.

***

> **Upgrading to v1.4.0?** Check out the new `wiki.html` included or use the web editor to switch to Simple Mode! _Requires `role.operator` or `hybroadcaster.admin` permission._
