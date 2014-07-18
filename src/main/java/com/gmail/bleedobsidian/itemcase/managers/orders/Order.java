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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an order.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class Order {

    /**
     * Itemcase involved.
     */
    private final Itemcase itemcase;

    /**
     * ItemStack being bought/sold.
     */
    private final ItemStack item;

    /**
     * Amount.
     */
    private int amount = 1;

    /**
     * Bukkit taskID.
     */
    private final int taskID;

    /**
     * New order.
     *
     * @param itemcase Itemcase involved.
     * @param player Player involved.
     * @param item ItemStack being bought/sold.
     */
    public Order(Itemcase itemcase, final Player player, ItemStack item) {
        this.itemcase = itemcase;
        this.item = item;

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

    /**
     * Cancel order.
     */
    public void cancel() {
        if (Bukkit.getScheduler().isQueued(this.taskID)) {
            Bukkit.getScheduler().cancelTask(this.taskID);
        }
    }

    /**
     * @return Itemcase involved.
     */
    public Itemcase getItemcase() {
        return itemcase;
    }

    /**
     * @return Amount of items to be bought/sold.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount Amount of items to be bought/sold.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return ItemStack being bought/sold.
     */
    public ItemStack getItem() {
        return item;
    }
}
