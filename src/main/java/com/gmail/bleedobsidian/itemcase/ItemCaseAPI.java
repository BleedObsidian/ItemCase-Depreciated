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

package com.gmail.bleedobsidian.itemcase;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.bleedobsidian.itemcase.managers.interfaces.SelectionListener;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

/**
 * Central ItemCase API.
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemCaseAPI {
    private final ItemCase itemcase;

    /**
     * Create new API instance.
     * 
     * @param itemcase
     *            - ItemCase plugin.
     */
    public ItemCaseAPI(ItemCase itemcase) {
        this.itemcase = itemcase;
    }

    /**
     * Create new Itemcase.
     * 
     * @param itemStack
     *            - ItemStack used for itemcase.
     * @param blockLocation
     *            - Bukkit block location.
     * @param player
     *            - Player creator.
     * @return - Itemcase. (Null if fails to create)
     */
    public Itemcase createItemcase(ItemStack itemStack, Location blockLocation,
            Player player) {
        return this.itemcase.getItemcaseManager().createItemcase(itemStack,
                blockLocation, player);
    }

    /**
     * Destroy itemcase.
     * 
     * @param itemcase
     *            - Itemcase to destroy.
     * @return - Boolean if successful.
     */
    public boolean destroyItemcase(Itemcase itemcase) {
        return this.itemcase.getItemcaseManager().destroyItemcase(itemcase,
                null);
    }

    /**
     * Destroy itemcase.
     * 
     * @param blockLocation
     *            - Bukkit block location of itemcase.
     * @return - Boolean if successful.
     */
    public boolean destroyItemcase(Location blockLocation) {
        if (this.itemcase.getItemcaseManager().isItemcaseAt(blockLocation)) {
            return this.itemcase.getItemcaseManager().destroyItemcase(
                    this.itemcase.getItemcaseManager().getItemcaseAt(
                            blockLocation), null);
        } else {
            return false;
        }
    }

    /**
     * @param blockLocation
     *            - Bukkit block location.
     * @return - Itemcase at location. (Null if doesn't exist)
     */
    public Itemcase getItemcaseAt(Location blockLocation) {
        return this.itemcase.getItemcaseManager().getItemcaseAt(blockLocation);
    }

    /**
     * @param blockLocation
     *            - Bukkit block location.
     * @return - If itemcase exists.
     */
    public boolean isItemcaseAt(Location blockLocation) {
        return this.itemcase.getItemcaseManager().isItemcaseAt(blockLocation);
    }

    /**
     * Add a pending itemcase selection.
     * 
     * @param listener
     *            - Listener.
     * @param player
     *            - Player.
     */
    public void addPendingSelection(SelectionListener listener, Player player) {
        this.itemcase.getSelectionManager().addPendingSelection(listener,
                player);
    }

    /**
     * Remove a pending itemcase selection.
     * 
     * @param player
     *            - Player
     */
    public void removePendingSelection(Player player) {
        this.itemcase.getSelectionManager().removePendingSelection(player);
    }

    /**
     * If player is pending an itemcase selection.
     * 
     * @param player
     *            - Player.
     * @return - If pending selection or not.
     */
    public boolean isPendingSelection(Player player) {
        return this.itemcase.getSelectionManager().isPendingSelection(player);
    }

    /**
     * Add pending itemcase order/transaction.
     * 
     * @param itemcase
     *            - Itemcase.
     * @param player
     *            - Player.
     */
    public void addPendingOrder(Itemcase itemcase, Player player) {
        this.itemcase.getShopManager().addPendingOrder(itemcase, player);
    }

    /**
     * Remove pending itemcase order/transaction.
     * 
     * @param player
     *            - Player.
     */
    public void removePendingOrder(Player player) {
        this.itemcase.getShopManager().removePendingOrder(player);
    }

    /**
     * If player has a current pending itemcase order/transaction.
     * 
     * @param player
     *            - Player.
     * @return - If player has current pending order.
     */
    public boolean isPendingOrder(Player player) {
        return this.itemcase.getShopManager().isPendingOrder(player);
    }
}
