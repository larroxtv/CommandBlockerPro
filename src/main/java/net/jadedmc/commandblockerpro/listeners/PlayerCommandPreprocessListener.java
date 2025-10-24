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
package net.jadedmc.commandblockerpro.listeners;

import net.jadedmc.commandblockerpro.CommandBlockerProPlugin;
import net.jadedmc.commandblockerpro.events.CommandBlockEvent;
import net.jadedmc.commandblockerpro.rules.Rule;
import net.jadedmc.commandblockerpro.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listens to the PlaceCommandPreprocessEvent, which runs when a player goes to send a command.
 * We use this to block commands set in the config.yml.
 */
public class PlayerCommandPreprocessListener implements Listener {
    private final CommandBlockerProPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerCommandPreprocessListener(@NotNull final CommandBlockerProPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlaceCommandPreprocessEvent.
     */
    @EventHandler
    public void onCommandSend(@NotNull final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String command = event.getMessage();

        // Loop through each rule configured, blocking the command if the rule catches it.
        for(final Rule rule : plugin.getRuleManager().getRules()) {
            if(rule.shouldBlock(player, command)) {
                // Calls the CommandBlockEvent.
                final CommandBlockEvent commandBlockEvent = new CommandBlockEvent(player, command, rule);
                plugin.getServer().getPluginManager().callEvent(commandBlockEvent);

                // If the CommandBlockEvent is cancelled, allow the command to be processed.
                if(commandBlockEvent.isCancelled()) {
                    return;
                }

                // Otherwise, block the command.
                event.setCancelled(true);

                // Display the block message is the rule has one.
                if(rule.hasBlockMessage()) {
                    ChatUtils.chat(player, rule.getBlockMessage());
                }

                // Play the block sound if the rule has one.
                if(rule.hasBlockSound()) {
                    player.playSound(player.getLocation(), rule.getBlockSound(), rule.getBlockSoundVolume(), rule.getBlockSoundPitch());
                }
            }
        }
    }
}