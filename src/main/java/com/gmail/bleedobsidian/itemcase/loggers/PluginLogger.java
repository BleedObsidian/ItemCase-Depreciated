/*
 * Copyright (C) 2013 Jesse Prescott <BleedObsidian@gmail.com>
 *
 * ItemCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package com.gmail.bleedobsidian.itemcase.loggers;

import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class allows you to easily send messages to the console of a JavaPlugin.
 * (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class PluginLogger {

    /**
     * JavaPlugin.
     */
    private static JavaPlugin plugin;

    /**
     * Set the JavaPlugin to use for the logger.
     *
     * @param plugin JavaPlugin to use logger from.
     */
    public static void setJavaPlugin(JavaPlugin plugin) {
        PluginLogger.plugin = plugin;
    }

    /**
     * Send info message to console.
     *
     * @param message Message to send.
     */
    public static void info(String message) {
        PluginLogger.plugin.getLogger().info(message);
    }

    /**
     * Send warning message to console.
     *
     * @param message Message to send.
     */
    public static void warning(String message) {
        PluginLogger.plugin
                .getServer()
                .getConsoleSender()
                .sendMessage(
                        "[" + PluginLogger.plugin.getDescription().getPrefix()
                        + "] " + ChatColor.YELLOW + message);
    }

    /**
     * Send warning message to console in red or not.
     *
     * @param message Message to send.
     * @param red If message should be red.
     */
    public static void warning(String message, boolean red) {
        if (red) {
            PluginLogger.plugin
                    .getServer()
                    .getConsoleSender()
                    .sendMessage(
                            "["
                            + PluginLogger.plugin.getDescription()
                            .getPrefix() + "] " + ChatColor.RED
                            + message);
        } else {
            PluginLogger.warning(message);
        }
    }

    /**
     * Send error message to console.
     *
     * @param message Message to send.
     */
    public static void error(String message) {
        PluginLogger.plugin.getLogger().severe(message);
    }

    /**
     * Send error message to console along with exception.
     *
     * @param message Message to send.
     * @param exception Exception to display.
     */
    public static void error(String message, Exception exception) {
        PluginLogger.plugin.getLogger().log(Level.SEVERE, message, exception);
    }
}
