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

import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.events.ItemcasePreTransactionEvent;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.orders.Order;
import com.gmail.bleedobsidian.itemcase.util.ShopGUI;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A manager to handle all pending orders and transactions. (Only used
 * internally, please use API)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ShopManager {

    /**
     * Current orders.
     */
    private final HashMap<Player, Order> orders = new HashMap<Player, Order>();

    /**
     * Add new pending order.
     *
     * @param itemcase Itemcase.
     * @param player Player.
     */
    public void addPendingOrder(Itemcase itemcase, Player player) {
        if (!Vault.isLoaded()) {
            PlayerLogger.messageLanguage(player,
                    "Player.ItemCase.Vault-Warning");
            return;
        }

        Order order = new Order(itemcase, player, itemcase.getItemStack());

        ItemcasePreTransactionEvent event = new ItemcasePreTransactionEvent(
                itemcase, player, order);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ShopGUI.displayGUI(itemcase, player, order);
            this.orders.put(player, order);
        }
    }

    /**
     * Remove pending order.
     *
     * @param player Player.
     */
    public void removePendingOrder(Player player) {
        this.getOrder(player).cancel();
        this.orders.remove(player);
        ShopGUI.displayCancelGUI(player);
    }

    /**
     * If player has a current pending order.
     *
     * @param player Player.
     * @return If player has current pending order.
     */
    public boolean isPendingOrder(Player player) {
        return this.orders.containsKey(player);
    }

    /**
     * Get current pending order.
     *
     * @param player Player.
     * @return Current pending order (Null if no order pending)
     */
    public Order getOrder(Player player) {
        if (this.isPendingOrder(player)) {
            return this.orders.get(player);
        } else {
            return null;
        }
    }

    /**
     * Get all current orders.
     *
     * @return List of all orders.
     */
    public HashMap<Player, Order> getOrders() {
        return this.orders;
    }
}
