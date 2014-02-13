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

/**
 * An event that is fired when an Itemcase is created.
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemcaseCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private final Itemcase itemcase;
    private final Player player;

    /**
     * New ItemcaseCreateEvent.
     * 
     * @param itemcase
     *            - Itemcase involved.
     * @param player
     *            - Player involved.
     */
    public ItemcaseCreateEvent(Itemcase itemcase, Player player) {
        this.itemcase = itemcase;
        this.player = player;
    }

    /**
     * @return - Itemcase created.
     */
    public Itemcase getItemcase() {
        return itemcase;
    }

    /**
     * @return - Player who created it.
     */
    public Player getPlayer() {
        return player;
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
