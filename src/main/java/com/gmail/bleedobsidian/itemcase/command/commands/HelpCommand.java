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

package com.gmail.bleedobsidian.itemcase.command.commands;

import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;

/**
 * Help Command. (Only used internally)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class HelpCommand {
    /**
     * Run command.
     * 
     * @param plugin
     *            - ItemCase plugin.
     * @param player
     *            - Player.
     * @param args
     *            - Arguments.
     */
    public static void help(ItemCase plugin, Player player, String[] args) {
        LanguageFile language = Language.getLanguageFile();

        if (!player.hasPermission("itemcase.help")) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Permission"));
            return;
        }

        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message1"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message2"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message3"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message4"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message5"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message6"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message7"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message8"));
        PlayerLogger.message(player,
                language.getMessage("Player.Help.Message9"));

        return;
    }
}
