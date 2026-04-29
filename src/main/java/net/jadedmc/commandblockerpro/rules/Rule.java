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
package net.jadedmc.commandblockerpro.rules;

import net.jadedmc.commandblockerpro.CommandBlockerPro;
import net.jadedmc.commandblockerpro.utils.CommandUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a pattern to follow when determining what commands should be blocked or hidden from a player.
 * Stores:
 *   - Rule Type (Blacklist, Hide, or Whitelist)
 *   - A bypass permission node (which allows players to ignore the rule if they have)
 *   - A list of commands the rule should act on.
 */
@SuppressWarnings("unused")
public class Rule {
    private final RuleType type;
    private final String bypassPermission;
    private final List<String> commands = new ArrayList<>();
    private final List<String> contains = new ArrayList<>();
    private final List<String> regex = new ArrayList<>();
    private final boolean hasBlockMessage;
    private final String blockMessage;
    private String blockSoundName;
    private float blockSoundVolume;
    private float blockSoundPitch;
    private boolean hasBlockSound;

    /**
     * Creates the rule using a configuration section.
     * @param config Configuration section storing the rule settings.
     */
    public Rule(final ConfigurationSection config) {
        type = RuleType.valueOf(config.getString("type"));

        if(config.isSet("bypassPermission")) {
            bypassPermission = config.getString("bypassPermission");
        }
        else {
            bypassPermission = "commandblocker.admin";
        }

        // Loop for applicable commands.
        if(config.isSet("commands")) {
            for(String command : config.getStringList("commands")) {
                commands.add(command.toLowerCase());
            }
        }

        // Loop for applicable contains strings.
        if(config.isSet("contains")) {
            for(String containedString : config.getStringList("contains")) {
                contains.add(containedString.toLowerCase());
            }
        }

        // Loop for regex filters.
        if(config.isSet("regex")) {
            regex.addAll(config.getStringList("regex"));
        }

        // Look for block message.
        if(config.isSet("blockMessage")) {
            hasBlockMessage = true;
            blockMessage = config.getString("blockMessage");
        }
        else {
            hasBlockMessage = false;
            blockMessage = "";
        }

        // Look for block sounds.
        if(config.isSet("blockSound.sound")) {
            hasBlockSound = true;
            blockSoundName = config.getString("blockSound.sound");

            blockSoundPitch = config.isSet("blockSound.pitch")
                ? (float) config.getDouble("blockSound.pitch")
                : 1.0f;

            blockSoundVolume = config.isSet("blockSound.volume")
                ? (float) config.getDouble("blockSound.volume")
                : 1.0f;
        }
        else {
            hasBlockSound = false;
            blockSoundName = "minecraft:entity.experience_orb.pickup";
            blockSoundPitch = 0;
            blockSoundVolume = 0;
        }

        // Register the dummy commands if the Rule Type is MESSAGE.
        if(this.type == RuleType.MESSAGE) {
            CommandUtils.registerDummyCommands(this);
        }
    }

    /**
     * Get the block message of the rule.
     * Returns an empty String if one isn't set.
     * @return Block Message of the rule.
     */
    public String getBlockMessage() {
        if(hasBlockMessage) {
            return blockMessage;
        }

        return CommandBlockerPro.getGlobalBlockMessage();
    }

    /**
     * Get the block sound of the rule.
     * Returns a default sound if one isn't set.
     * @return Block Sound of the rule.
     */
    public Sound getBlockSound() {
        if(hasBlockSound && blockSoundName != null) {
            try {
                return Registry.SOUNDS.get(Key.key(blockSoundName));
            } catch (Exception e) {
                // Fallback to default
                return CommandBlockerPro.getGlobalBlockSound();
            }
        }

        return CommandBlockerPro.getGlobalBlockSound();
    }

    /**
     * Gets the Pitch of the Block Sound.
     * Returns 1.0 if not set.
     * @return Block Sound Pitch.
     */
    public float getBlockSoundPitch() {
        if(hasBlockSound) {
            return blockSoundPitch;
        }
        return CommandBlockerPro.getGlobalBlockSoundPitch();
    }

    /**
     * Returns the Volume of the Block Sound.
     * Returns 1.0 if not set.
     * @return Block Sound Volume.
     */
    public float getBlockSoundVolume() {
        if(hasBlockSound) {
            return blockSoundVolume;
        }
        return CommandBlockerPro.getGlobalBlockSoundVolume();
    }

    /**
     * Get the permission node required to bypass the rule.
     * @return Rule's bypass permission node.
     */
    public String getBypassPermission() {
        return bypassPermission;
    }

    /**
     * Get all commands stored by the rule.
     * @return The rule's stored commands.
     */
    public Collection<String> getCommands() {
        return commands;
    }

    /**
     * Retrieves the type of the rule.
     * @return Rule Type.
     */
    public RuleType getType() {
        return type;
    }

    /**
     * Get if the rule has a block message set in its config.
     * @return Whether the rule has a block message configured.
     */
    public boolean hasBlockMessage() {
        return (this.hasBlockMessage || CommandBlockerPro.hasGlobalBlockMessage());
    }

    /**
     * Get if the rule has a block sound set in its config.
     * @return Whether the rule has a block sound configured.
     */
    public boolean hasBlockSound() {
        return (this.hasBlockSound || CommandBlockerPro.hasGlobalBlockSound());
    }

    /**
     * Determine if a rule blocks a given player from using a given command.
     * @param player Player trying to use the command.
     * @param command Command the player is trying to use.
     * @return Whether they can use the command or not.
     */
    public boolean shouldBlock(Player player, String command) {
        // Don't block if no permission was set.
        if(bypassPermission.isEmpty()) {
            return false;
        }

        // Only block if the player does not have the bypass permission.
        if(player.hasPermission(bypassPermission)) {
           return false;
        }

        // The hide rule does not block commands.
        if(type == RuleType.HIDE) {
            return false;
        }

        // prepare full and base command forms
        final String fullCommandLower = command.toLowerCase();
        final String baseCommandLower = fullCommandLower.split(" ")[0];

        // Check the type of the rule to determine if the command should be blocked.
        switch (type) {
            case BLACKLIST:

                // Check for contained strings.
                for(String containsString : contains) {
                    if(fullCommandLower.contains(containsString)) {
                        return true;
                    }
                }

                // Loops through each regex statement in the configured list.
                for(String filter : regex) {
                    Pattern pattern = Pattern.compile(filter);
                    Matcher matcher = pattern.matcher(command);

                    // Checks if there is a match.
                    if(matcher.find()) {
                        // If so, the command fails.
                        return true;
                    }
                }

                return commands.contains(baseCommandLower);

            case WHITELIST:
                // Check for contained strings.
                for(String containsString : contains) {
                    if(!fullCommandLower.contains(containsString)) {
                        return true;
                    }
                }

                // Loops through each regex statement in the configured list.
                for(String filter : regex) {
                    Pattern pattern = Pattern.compile(filter);
                    Matcher matcher = pattern.matcher(command);

                    // Checks if there is a match.
                    if(matcher.find()) {
                        // If so, the command fails.
                        return false;
                    }
                }

                return !commands.contains(baseCommandLower);
        }

        // If something goes wrong, don't block the command.
        return false;
    }

    /**
     * Determine if a rule blocks a given player from hiding a given command from tab complete.
     * @param player Player trying to tab complete the command.
     * @param command Command the player is trying to tab complete.
     * @return Whether they can tab complete the command or not.
     */
    public boolean shouldHide(Player player, String command) {
        // Don't hide if no permission was set.
        if(bypassPermission.isEmpty()) {
            return false;
        }

        // Only hide if the player does not have the bypass permission.
        if(player.hasPermission(bypassPermission)) {
            return false;
        }

        // prepare full and base command forms
        final String fullCommandLower = command.toLowerCase();
        final String baseCommandLower = fullCommandLower.split(" ")[0];

        // Check the type of the rule to determine if the command should be hidden.
        switch(type) {
            case BLACKLIST:
            case HIDE:
                // Check for contained strings.
                for(String containsString : contains) {
                    if(fullCommandLower.contains(containsString)) {
                        return true;
                    }
                }

                // Loops through each regex statement in the configured list.
                for(String filter : regex) {
                    Pattern pattern = Pattern.compile(filter);
                    Matcher matcher = pattern.matcher(command);

                    // Checks if there is a match.
                    if(matcher.find()) {
                        // If so, the command fails.
                        return true;
                    }
                }

                return commands.contains(baseCommandLower);

            case WHITELIST:
                // Check for contained strings.
                for(String containsString : contains) {
                    if(!fullCommandLower.contains(containsString)) {
                        return true;
                    }
                }

                // Loops through each regex statement in the configured list.
                for(String filter : regex) {
                    Pattern pattern = Pattern.compile(filter);
                    Matcher matcher = pattern.matcher(command);

                    // Checks if there is a match.
                    if(matcher.find()) {
                        // If so, the command fails.
                        return false;
                    }
                }

                return !commands.contains(baseCommandLower);
        }

        // If something goes wrong, don't hide the command.
        return false;
    }
}