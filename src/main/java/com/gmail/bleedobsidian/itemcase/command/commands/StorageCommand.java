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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.command.listeners.StorageSelectionListener;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;
import org.bukkit.entity.Player;

/**
 * Storage Command. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class StorageCommand {

    /**
     * Run command.
     *
     * @param player Player that ran command.
     * @param args Command arguments.
     */
    public static void storage(Player player, String[] args) {
        LanguageFile language = Language.getLanguageFile();

        if (!player.hasPermission("itemcase.create.shop.buy") && !player
                .hasPermission("itemcase.create.shop.sell") && !player.
                hasPermission("itemcase.create.pickup")) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Permission"));
            return;
        }

        StorageSelectionListener listener = new StorageSelectionListener();
        ItemCase.getInstance().getSelectionManager().addPendingSelection(
                listener, player);

        PlayerLogger.message(player,
                language.getMessage("Player.Storage.Select"));
        PlayerLogger.message(player,
                language.getMessage("Player.Storage.Cancel"));
    }

    /**
     * Received selection.
     *
     * @param player Player that selected an Itemcase.
     * @param itemcase Selected Itemcase.
     */
    public static void selected(Player player,
            Itemcase itemcase) {
        LanguageFile language = Language.getLanguageFile();

        if (!(itemcase.canBuy() || itemcase.canSell() || itemcase.getType() == ItemcaseType.PICKUP_POINT)) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Storage.No-Storage"));
            return;
        }

        if (itemcase.getType() == ItemcaseType.SHOP) {
            if (!(player.hasPermission("itemcase.create.shop.sell") || player.
                    hasPermission("itemcase.create.shop.buy"))) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Permission"));
                return;
            }
        }

        if (itemcase.getType() == ItemcaseType.PICKUP_POINT) {
            if (!player.hasPermission("itemcase.create.pickup")) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Permission"));
                return;
            }
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
        ItemCase.getInstance().getInventoryManager().addOpenInventory(player,
                itemcase);
    }
}
