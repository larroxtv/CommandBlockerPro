/*
 * This file is part of CommandBlockerPro, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.commandblockerpro;

import net.jadedmc.commandblockerpro.rules.Rule;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A collection of static methods for other plugins to interact with.
 */
@SuppressWarnings("unused")
public class CommandBlockerPro {
    private static CommandBlockerProPlugin plugin;

    /**
     * Get if a global block message has been configured.
     * @return true is yes, false if not.
     */
    public static boolean hasGlobalBlockMessage() {
        return plugin.getConfigManager().getConfig().isSet("blockMessage");
    }

    /**
     * Get if a global block sound has been configured.
     * @return true if yes, false it not.
     */
    public static boolean hasGlobalBlockSound() {
        return plugin.getConfigManager().getConfig().isSet("blockSound.sound");
    }

    /**
     * Get the currently configured global block message.
     * @return The configured block message as a String, or null if not set.
     */
    @Nullable
    public static String getGlobalBlockMessage() {
        if(plugin.getConfigManager().getConfig().isSet("blockMessage")) {
            return plugin.getConfigManager().getConfig().getString("blockMessage");
        }

        return null;
    }

    /**
     * Get the currently configured global block sound.
     * @return Sound configured, or null if not set.
     */
    @Nullable
    public static Sound getGlobalBlockSound() {
        if(plugin.getConfigManager().getConfig().isSet("blockSound.sound")) {
            try {
                String soundName = plugin.getConfigManager().getConfig().getString("blockSound.sound");
                return Registry.SOUNDS.get(Key.key(soundName));
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Get the configured pitch of the global block sound.
     * @return the configured pitch of the block sound as a float, or 1.0f if not set.
     */
    public static float getGlobalBlockSoundPitch() {
        if (plugin.getConfigManager().getConfig().isSet("blockSound.pitch")) {
            return (float) plugin.getConfigManager().getConfig().getDouble("blockSound.pitch");
        }

        return 1.0f;
    }

    /**
     * Get the configured volume of the global block sound.
     * @return the configured volume of the block sound as a float, or 1.0f if not set.
     */
    public static float getGlobalBlockSoundVolume() {
        if (plugin.getConfigManager().getConfig().isSet("blockSound.volume")) {
            return  (float) plugin.getConfigManager().getConfig().getDouble("blockSound.volume");
        }

        return 1.0f;
    }

    /**
     *  Get all currently loaded rules.
     * @return Collection of all loaded rules.
     */
    public static Collection<Rule> getRules() {
        return plugin.getRuleManager().getRules();
    }

    /**
     * Passes an instance of the plugin to this static class.
     * @param pl Instance of the plugin.
     */
    protected static void setPlugin(final CommandBlockerProPlugin pl) {
        plugin = pl;
    }
}