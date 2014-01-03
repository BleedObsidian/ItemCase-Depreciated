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

package com.gmail.bleedobsidian.itemcase.managers;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;

public class AmountManager {
    private final ItemCase plugin;

    private ArrayList<Player> pendingAmounts = new ArrayList<Player>();

    public AmountManager(ItemCase plugin) {
        this.plugin = plugin;
    }

    public void addPendingAmount(Player player) {
        this.pendingAmounts.add(player);
    }

    public void removePendingAmount(Player player) {
        this.pendingAmounts.remove(player);
    }

    public void setPendingAmount(Player player, int amount) {
        this.plugin.getShopManager().getOrder(player).setAmount(amount);
        this.removePendingAmount(player);

        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.Order.Amount-Set",
                        new String[] { "%Amount%", "" + amount }));
        PlayerLogger.message(player,
                Language.getLanguageFile()
                        .getMessage("Player.Order.Amount-End"));
    }

    public boolean isPendingAmount(Player player) {
        return this.pendingAmounts.contains(player) ? true : false;
    }
}
