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

import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 * A manager to keep track of open inventories for ItemCase. (Only used
 * internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class InventoryManager {

    /**
     * Open Itemcase inventories.
     */
    private final HashMap<Player, Itemcase> openInventories = new HashMap<Player, Itemcase>();

    /**
     * Add inventory to open list.
     *
     * @param player Player.
     * @param itemcase Itemcase.
     */
    public void addOpenInventory(Player player, Itemcase itemcase) {
        this.openInventories.put(player, itemcase);
    }

    /**
     * Remove inventory from open list.
     *
     * @param player Player.
     */
    public void removeOpenInventory(Player player) {
        this.openInventories.remove(player);
    }

    /**
     * @param player Player.
     * @return If player is viewing inventory.
     */
    public boolean hasOpenInventory(Player player) {
        return this.openInventories.containsKey(player);
    }

    /**
     * @param player Player.
     * @return Get Itemcase from open inventory.
     */
    public Itemcase getItemcaseForPlayer(Player player) {
        return this.openInventories.get(player);
    }
}
