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
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when an Itemcase is destroyed.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemcaseDestroyEvent extends Event implements Cancellable {

    /**
     * HandlerList.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * If event is cancelled.
     */
    private boolean isCancelled;

    /**
     * Itemcase that was destroyed.
     */
    private final Itemcase itemcase;

    /**
     * Player that destroyed Itemcase.
     */
    private final Player player;

    /**
     * New ItemcaseDestroyEvent.
     *
     * @param itemcase Itemcase that was destroyed.
     * @param player Player that destroyed Itemcase.
     */
    public ItemcaseDestroyEvent(Itemcase itemcase, Player player) {
        this.itemcase = itemcase;
        this.player = player;
    }

    /**
     * @return - Itemcase that was destroyed.
     */
    public Itemcase getItemcase() {
        return itemcase;
    }

    /**
     * @return - Player that destroyed Itemcase.
     */
    public Player getPlayer() {
        return player;
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
