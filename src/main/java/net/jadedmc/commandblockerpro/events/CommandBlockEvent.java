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
package net.jadedmc.commandblockerpro.events;

import net.jadedmc.commandblockerpro.rules.Rule;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * This even is called when a player is blocked from using a command.
 */
@SuppressWarnings("unused")
public class CommandBlockEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String command;
    private final Rule rule;
    private boolean cancelled = false;

    /**
     * Creates the event.
     * @param player Player whose command got blocked.
     * @param command Command that got blocked.
     * @param rule The rule that blocked the command.
     */
    public CommandBlockEvent(final Player player, String command, Rule rule) {
        this.player = player;
        this.command = command;
        this.rule = rule;
    }

    /**
     * Get the command that was blocked.
     * @return Command that was blocked.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the player who was blocked.
     * @return Player who was blocked.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the rule that blocked the command.
     * @return Rule that blocked the command.
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Get all event handlers.
     * @return Event handlers.
     */
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Get all event handlers.
     * @return Event handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Check if the event is cancelled.
     * @return Whether the event is cancelled.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set if the event should be cancelled.
     * @param cancelled If the event should be cancelled.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}