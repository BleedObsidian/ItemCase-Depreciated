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

package com.gmail.bleedobsidian.itemcase.tasks;

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

/**
 * A bukkit task to watch and monitor all itemcases. (Only used internally)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemcaseWatcher implements Runnable {
    private ItemcaseManager itemcaseManager;

    /**
     * New ItemcaseWatcher.
     * 
     * @param itemcaseManager
     *            - ItemcaseManager.
     */
    public ItemcaseWatcher(ItemcaseManager itemcaseManager) {
        this.itemcaseManager = itemcaseManager;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        for (Itemcase itemcase : this.itemcaseManager.getItemcases()) {
            if (itemcase.getItem().isDead()) {
                itemcase.spawnItem();
            }

            Location itemLocation = itemcase.getItem().getLocation();
            Location blockLocation = itemcase.getBlock().getLocation();

            if (!((itemLocation.getX() == blockLocation.getBlockX() + 0.5)
                    && ((itemLocation.getY() <= blockLocation.getBlockY() + 2) && (itemLocation
                            .getY() >= blockLocation.getBlockY())) && (itemLocation
                        .getZ() == blockLocation.getBlockZ() + 0.5))) {
                itemcase.despawnItem();
                itemcase.spawnItem();
            }

            if (itemcase.getItem().getItemStack().getAmount() > 1) {
                itemcase.getItem().getItemStack().setAmount(1);
            }

            if (!ItemCase.getInstance().getConfigFile().getFileConfiguration()
                    .getIntegerList("Blocks")
                    .contains(itemcase.getBlock().getType().getId())) {
                itemcase.getBlock().setType(Material.STEP);
            }
        }
    }
}
