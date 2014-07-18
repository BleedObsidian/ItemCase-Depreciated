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
package com.gmail.bleedobsidian.itemcase.configurations;

import java.io.InputStream;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class allows you to load and parse a language file.
 *
 * @author Jesse Prescott
 */
public class LanguageFile {

    /**
     * FileConfiguration.
     */
    private FileConfiguration fileConfiguration;

    /**
     * Name of language file.
     */
    private final String name;

    /**
     * New LanguageFile with given name.
     *
     * @param name Name of language file.
     */
    public LanguageFile(String name) {
        this.name = name;
    }

    /**
     * Load language file from folder.
     *
     * @param plugin JavaPlugin.
     */
    public void load(JavaPlugin plugin) {
        InputStream stream = plugin.getResource("languages/" + this.name);
        this.fileConfiguration = YamlConfiguration.loadConfiguration(stream);
    }

    /**
     * Get parsed version of message within language file with given variables.
     *
     * @param path Path of message.
     * @param variables Variables to change.
     * @return Parsed Message.
     */
    public String getMessage(String path, String[] variables) {
        return this.parseMessage(this.fileConfiguration.getString(path),
                variables);
    }

    /**
     * Get parsed version of message within language file.
     *
     * @param path Path of message.
     * @return Parsed Message.
     */
    public String getMessage(String path) {
        return this.parseMessage(this.fileConfiguration.getString(path),
                new String[0]);
    }

    /**
     * Parse message with given variables.
     *
     * @param message Message to parse.
     * @param variables Variables to change.
     * @return Parsed Message.
     */
    private String parseMessage(String message, String[] variables) {
        message = message.replaceAll("\\(BLACK\\)", ChatColor.BLACK.toString());

        message = message.replaceAll("\\(AQUA\\)", ChatColor.AQUA.toString());
        message = message.replaceAll("\\(DARK_AQUA\\)",
                ChatColor.DARK_AQUA.toString());

        message = message.replaceAll("\\(BLUE\\)", ChatColor.BLUE.toString());
        message = message.replaceAll("\\(DARK_BLUE\\)",
                ChatColor.DARK_BLUE.toString());

        message = message.replaceAll("\\(GREEN\\)", ChatColor.GREEN.toString());
        message = message.replaceAll("\\(DARK_GREEN\\)",
                ChatColor.DARK_GREEN.toString());

        message = message.replaceAll("\\(RED\\)", ChatColor.RED.toString());
        message = message.replaceAll("\\(DARK_RED\\)",
                ChatColor.DARK_RED.toString());

        message = message.replaceAll("\\(GRAY\\)", ChatColor.GRAY.toString());
        message = message.replaceAll("\\(DARK_GRAY\\)",
                ChatColor.DARK_GRAY.toString());

        message = message.replaceAll("\\(LIGHT_PURPLE\\)",
                ChatColor.LIGHT_PURPLE.toString());
        message = message.replaceAll("\\(DARK_PURPLE\\)",
                ChatColor.DARK_PURPLE.toString());

        message = message.replaceAll("\\(GOLD\\)", ChatColor.GOLD.toString());

        message = message.replaceAll("\\(YELLOW\\)",
                ChatColor.YELLOW.toString());

        message = message.replaceAll("\\(WHITE\\)", ChatColor.WHITE.toString());

        message = message.replaceAll("\\(BOLD\\)", ChatColor.BOLD.toString());
        message = message.replaceAll("\\(ITALIC\\)",
                ChatColor.ITALIC.toString());

        message = message.replaceAll("\\(MAGIC\\)", ChatColor.MAGIC.toString());

        message = message.replaceAll("\\(RESET\\)", ChatColor.RESET.toString());

        for (int i = 0; i < variables.length; i += 2) {
            message = message.replaceAll(variables[i], variables[i + 1]);
        }

        return message.trim();
    }
}
