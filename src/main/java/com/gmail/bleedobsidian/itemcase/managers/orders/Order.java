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

package com.gmail.bleedobsidian.itemcase.managers.orders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

public class Order {
    private final Itemcase itemcase;
    private final int taskID;
    private int amount = 1;

    public Order(Itemcase itemcase, final Player player) {
        this.itemcase = itemcase;

        this.taskID = Bukkit.getScheduler()
                .runTaskLater(ItemCase.getInstance(), new Runnable() {
                    public void run() {
                        if (ItemCase.getInstance().getShopManager()
                                .isPendingOrder(player)) {
                            PlayerLogger.message(
                                    player,
                                    Language.getLanguageFile().getMessage(
                                            "Player.Order.Timeout"));
                            PlayerLogger.message(
                                    player,
                                    Language.getLanguageFile().getMessage(
                                            "Player.Order.Amount-End"));
                            ItemCase.getInstance().getShopManager()
                                    .removePendingOrder(player);
                        }
                    }
                }, 600).getTaskId();
    }

    public void cancel() {
        if (Bukkit.getScheduler().isQueued(this.taskID)) {
            Bukkit.getScheduler().cancelTask(this.taskID);
        }
    }

    public Itemcase getItemcase() {
        return itemcase;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
