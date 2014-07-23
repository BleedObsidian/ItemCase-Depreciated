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
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import org.bukkit.entity.Player;

/**
 * Cancel command. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class CancelCommand {

    /**
     * Run command.
     *
     * @param player Player that ran command.
     * @param args Command arguments.
     */
    public static void cancel(Player player, String[] args) {
        if (ItemCase.getInstance().getSelectionManager().isPendingSelection(
                player)) {
            ItemCase.getInstance().getSelectionManager().removePendingSelection(
                    player);

            PlayerLogger.messageLanguage(player, "Player.Cancel.Canceled");
            return;
        } else if (ItemCase.getInstance().getShopManager().
                isPendingOrder(player)) {
            PlayerLogger.messageLanguage(player, "Player.Order.Canceled");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
            ItemCase.getInstance().getShopManager().removePendingOrder(player);
            ItemCase.getInstance().getInputManager().removePendingInput(player);
            return;
        } else {
            PlayerLogger.messageLanguage(player, "Player.Cancel.Error");
            return;
        }
    }
}
