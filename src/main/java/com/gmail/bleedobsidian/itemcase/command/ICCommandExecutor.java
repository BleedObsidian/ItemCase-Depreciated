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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.command.commands.CancelCommand;
import com.gmail.bleedobsidian.itemcase.command.commands.CreateCommand;
import com.gmail.bleedobsidian.itemcase.command.commands.HelpCommand;
import com.gmail.bleedobsidian.itemcase.command.commands.ModifyCommand;
import com.gmail.bleedobsidian.itemcase.command.commands.OrderCommand;
import com.gmail.bleedobsidian.itemcase.command.commands.StorageCommand;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.loggers.PluginLogger;

/**
 * Main command executor. (Only used internally)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class ICCommandExecutor implements CommandExecutor {
    private final ItemCase plugin;

    /**
     * Create new command executor.
     * 
     * @param plugin
     *            - ItemCase plugin.
     */
    public ICCommandExecutor(ItemCase plugin) {
        this.plugin = plugin;
    }

    /**
     * On command execute.
     */
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
        if (args[0].equalsIgnoreCase("create")) {
            CreateCommand.create(plugin, player, args);
        } else if (args[0].equalsIgnoreCase("modify")) {
            ModifyCommand.modify(plugin, player, args);
        } else if (args[0].equalsIgnoreCase("order")) {
            OrderCommand.order(plugin, player, args);
        } else if (args[0].equalsIgnoreCase("storage")) {
            StorageCommand.storage(plugin, player, args);
        } else if (args[0].equalsIgnoreCase("cancel")) {
            CancelCommand.cancel(plugin, player, args);
        } else if (args[0].equalsIgnoreCase("help")) {
            HelpCommand.help(plugin, player, args);
        } else {
            return false;
        }

        return true;
    }
}
