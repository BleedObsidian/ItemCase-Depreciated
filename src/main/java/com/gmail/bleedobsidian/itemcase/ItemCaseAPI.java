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

import com.gmail.bleedobsidian.itemcase.managers.interfaces.SelectionListener;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Central ItemCase API.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemCaseAPI {

    /**
     * Create new Itemcase with given ItemStack at given Location.
     *
     * @param itemStack ItemStack used for Itemcase.
     * @param blockLocation Bukkit block location.
     * @param player Player creator (Owner).
     * @return Itemcase. (Null if fails to create new Itemcase)
     */
    public Itemcase createItemcase(ItemStack itemStack, Location blockLocation,
            Player player) {
        return ItemCase.getInstance().getItemcaseManager().createItemcase(
                itemStack,
                blockLocation, player);
    }

    /**
     * Destroy Itemcase.
     *
     * @param itemcase Itemcase to destroy.
     * @return If successful.
     */
    public boolean destroyItemcase(Itemcase itemcase) {
        return ItemCase.getInstance().getItemcaseManager().destroyItemcase(
                itemcase,
                null);
    }

    /**
     * Destroy Itemcase at given Location.
     *
     * @param blockLocation Bukkit block location of Itemcase.
     * @return Boolean if successful.
     */
    public boolean destroyItemcase(Location blockLocation) {
        if (ItemCase.getInstance().getItemcaseManager().isItemcaseAt(
                blockLocation)) {
            return ItemCase.getInstance().getItemcaseManager().destroyItemcase(
                    ItemCase.getInstance().getItemcaseManager().getItemcaseAt(
                            blockLocation), null);
        } else {
            return false;
        }
    }

    /**
     * Get ItemCase at given Location.
     *
     * @param blockLocation Bukkit block location.
     * @return Itemcase at location. (Null if ItemCase doesn't exist)
     */
    public Itemcase getItemcaseAt(Location blockLocation) {
        return ItemCase.getInstance().getItemcaseManager().getItemcaseAt(
                blockLocation);
    }

    /**
     * If ItemCase is present at given Location.
     *
     * @param blockLocation Bukkit block location.
     * @return If Itemcase exists.
     */
    public boolean isItemcaseAt(Location blockLocation) {
        return ItemCase.getInstance().getItemcaseManager().isItemcaseAt(
                blockLocation);
    }

    /**
     * Add a pending Itemcase selection. (When a player selects an ItemCase)
     *
     * @param listener SelectionListener.
     * @param player Player to apply selection too.
     */
    public void addPendingSelection(SelectionListener listener, Player player) {
        ItemCase.getInstance().getSelectionManager().addPendingSelection(
                listener,
                player);
    }

    /**
     * Remove a pending Itemcase selection.
     *
     * @param player Player to remove any pending selections from.
     */
    public void removePendingSelection(Player player) {
        ItemCase.getInstance().getSelectionManager().removePendingSelection(
                player);
    }

    /**
     * If player is pending an Itemcase selection.
     *
     * @param player Player.
     * @return If pending selection or not.
     */
    public boolean isPendingSelection(Player player) {
        return ItemCase.getInstance().getSelectionManager().isPendingSelection(
                player);
    }

    /**
     * Add pending Itemcase order/transaction.
     *
     * @param itemcase Itemcase.
     * @param player Player.
     */
    public void addPendingOrder(Itemcase itemcase, Player player) {
        ItemCase.getInstance().getShopManager().
                addPendingOrder(itemcase, player);
    }

    /**
     * Remove pending Itemcase order/transaction.
     *
     * @param player Player.
     */
    public void removePendingOrder(Player player) {
        ItemCase.getInstance().getShopManager().removePendingOrder(player);
    }

    /**
     * If player has a current pending Itemcase order/transaction.
     *
     * @param player Player.
     * @return If player has current pending order.
     */
    public boolean isPendingOrder(Player player) {
        return ItemCase.getInstance().getShopManager().isPendingOrder(player);
    }
}
