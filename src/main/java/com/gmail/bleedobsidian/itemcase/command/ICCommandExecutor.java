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

package com.gmail.bleedobsidian.itemcase.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.Main;
import com.gmail.bleedobsidian.itemcase.logger.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.logger.PluginLogger;

public class ICCommandExecutor implements CommandExecutor {
    private final Main plugin;

    public ICCommandExecutor(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 1) {
                if (this.proccessCommand(player, args)) {
                    return true;
                } else {
                    PlayerLogger.message(player, Language.getLanguageFile()
                            .getMessage("Player.Syntax-Error"));
                    return true;
                }
            } else {
                PlayerLogger.message(player, Language.getLanguageFile()
                        .getMessage("Player.Syntax-Error"));
                return true;
            }
        } else {
            PluginLogger.info(Language.getLanguageFile().getMessage(
                    "Console.Command"));
            return true;
        }
    }

    private boolean proccessCommand(Player player, String[] args) {
        return true;
    }
}
