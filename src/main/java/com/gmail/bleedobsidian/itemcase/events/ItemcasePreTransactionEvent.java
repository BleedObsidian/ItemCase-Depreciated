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

import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.orders.Order;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a player initiates a transaction with an
 * Itemcase. (Right clicks)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemcasePreTransactionEvent extends Event implements Cancellable {

    /**
     * HandlerList.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * If event is cancelled.
     */
    private boolean isCancelled;

    /**
     * Itemcase that was bought from.
     */
    private final Itemcase itemcase;

    /**
     * Player that bought from Itemcase.
     */
    private final Player player;

    /**
     * Order.
     */
    private final Order order;

    /**
     * New ItemcasePreTransactionEvent.
     *
     * @param itemcase Itemcase that was bought from.
     * @param player Player that bought from Itemcase.
     * @param order Order.
     */
    public ItemcasePreTransactionEvent(Itemcase itemcase, Player player,
            Order order) {
        this.itemcase = itemcase;
        this.player = player;
        this.order = order;
    }

    /**
     * @return Itemcase thats involved in transaction.
     */
    public Itemcase getItemcase() {
        return itemcase;
    }

    /**
     * @return Player that is involved in transaction.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Current order.
     */
    public Order getOrder() {
        return order;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return HandlerList.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return If event is cancelled.
     */
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * @param isCancelled If event is cancelled.
     */
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
