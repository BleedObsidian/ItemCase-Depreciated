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
import com.gmail.bleedobsidian.itemcase.command.listeners.StorageSelectionListener;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

/**
 * Storage Command. (Only used internally)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class StorageCommand {
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
    public static void storage(ItemCase plugin, Player player, String[] args) {
        LanguageFile language = Language.getLanguageFile();

        if (!(player.hasPermission("itemcase.create.shop.buy") || player
                .hasPermission("itemcase.create.shop.sell"))) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Permission"));
            return;
        }

        StorageSelectionListener listener = new StorageSelectionListener(plugin);
        plugin.getSelectionManager().addPendingSelection(listener, player);

        PlayerLogger.message(player,
                language.getMessage("Player.Storage.Select"));
        PlayerLogger.message(player,
                language.getMessage("Player.Storage.Cancel"));
    }

    /**
     * Received selection.
     * 
     * @param plugin
     *            - ItemCase plugin.
     * @param player
     *            - Player.
     * @param itemcase
     *            - Selected Itemcase.
     */
    public static void selected(ItemCase plugin, Player player,
            Itemcase itemcase) {
        LanguageFile language = Language.getLanguageFile();

        if (!(itemcase.canBuy() || itemcase.canSell())) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Storage.Not-Shop"));
            return;
        }

        if (!(itemcase.getOwnerName().equals(player.getName()) || player
                .hasPermission("itemcase.storage.other"))) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Storage.Storage-Permission"));
            return;
        }

        if (itemcase.isInfinite()) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Storage.Infinite"));
            return;
        }

        player.openInventory(itemcase.getInventory());
        plugin.getInventoryManager().addOpenInventory(player, itemcase);
    }
}
