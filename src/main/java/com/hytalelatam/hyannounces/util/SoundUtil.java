package com.hytalelatam.hyannounces.util;

import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hytalelatam.hyannounces.HyAnnouncesPlugin;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility for playing sounds to players.
 * Based on Essentials SoundUtil.
 */
public final class SoundUtil {

    private static final Set<String> WARNED_SOUNDS = ConcurrentHashMap.newKeySet();

    private SoundUtil() {
    }

    /**
     * Plays a sound to a specific player.
     *
     * @param playerRef The player to play the sound to
     * @param soundName The sound event name (e.g., "SFX_Item_Repair" or
     *                  "my_mod:custom_sound")
     */
    public static void playSound(@Nonnull PlayerRef playerRef, @Nonnull String soundName) {
        if (soundName == null || soundName.isEmpty())
            return;

        int soundIndex = SoundEvent.getAssetMap().getIndex(soundName);
        if (soundIndex == 0) {
            // Sound not found in registry (might be a typo or missing asset)
            if (WARNED_SOUNDS.add(soundName)) {
                HyAnnouncesPlugin.getInstance().getLogger().atWarning()
                        .log("[SoundSystem] Sound ID not found: '" + soundName + "'. Check your config.json");
            }
            return;
        }
        com.hypixel.hytale.server.core.universe.world.SoundUtil.playSoundEvent2dToPlayer(
                playerRef, soundIndex, SoundCategory.SFX);
    }
}
