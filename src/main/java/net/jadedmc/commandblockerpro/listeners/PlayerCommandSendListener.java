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
import net.jadedmc.commandblockerpro.rules.Rule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Listens to the PlayerCommandSendEvent, which runs every time the server sends the command list to the player.
 *
 */
public class PlayerCommandSendListener implements Listener {
    private final CommandBlockerProPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerCommandSendListener(@NotNull final CommandBlockerProPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerCommandSendEvent.
     */
    @EventHandler
    public void onCommandSend(@NotNull final PlayerCommandSendEvent event) {
        final Player player = event.getPlayer();
        final List<String> toRemove = new ArrayList<>();

        // Loops through all the rules.
        for(final Rule rule : plugin.getRuleManager().getRules()) {

            // Identifies commands that are hidden by that rule.
            for(final String command : event.getCommands()) {
                if(rule.shouldHide(player, "/" + command)) {
                    toRemove.add(command);
                }
            }
        }
        event.getCommands().removeAll(toRemove);
    }
}