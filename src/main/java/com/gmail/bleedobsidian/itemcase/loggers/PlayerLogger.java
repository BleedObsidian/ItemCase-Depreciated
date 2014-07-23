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

import com.gmail.bleedobsidian.itemcase.Language;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This class allows you to easily send messages to a player. (Only used
 * internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class PlayerLogger {

    /**
     * Prefix placed in front of every message.
     */
    private static String prefix = ChatColor.BLUE + "[ItemCase]";

    /**
     * Message player with given message.
     *
     * @param player Player to message.
     * @param message Message.
     */
    public static void message(Player player, String message) {
        player.sendMessage(prefix + ": " + ChatColor.RESET + message);
    }

    /**
     * Message player with given message.
     *
     * @param player Player to message.
     * @param languageMessage Language Message.
     */
    public static void messageLanguage(Player player, String languageMessage) {
        player.sendMessage(prefix + ": " + ChatColor.RESET + Language.
                getLanguageFile()
                .getMessage(
                        languageMessage));
    }
}
