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

package com.gmail.bleedobsidian.itemcase.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.orders.Order;

/**
 * An event that is fired when a player buys an item from an itemcase.
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemcaseBuyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private final Itemcase itemcase;
    private final Player player;
    private final Order order;

    /**
     * New ItemcaseBuyEvent.
     * 
     * @param itemcase
     *            - Itemcase.
     * @param player
     *            - Player.
     * @param order
     *            - Order.
     */
    public ItemcaseBuyEvent(Itemcase itemcase, Player player, Order order) {
        this.itemcase = itemcase;
        this.player = player;
        this.order = order;
    }

    /**
     * @return - Itemcase.
     */
    public Itemcase getItemcase() {
        return itemcase;
    }

    /**
     * @return - Player who bought.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return - Bought Order.
     */
    public Order getOrder() {
        return order;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
