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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.util.InventoryUtils;
import org.bukkit.Bukkit;

/**
 * A Bukkit task to spawn the item of a pickup point. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class PickupPointSpawner implements Runnable {

    /**
     * Itemcase to watch.
     */
    private final Itemcase itemcase;

    /**
     * New PickupPointWatcher.
     *
     * @param itemcase Itemcase to watch.
     */
    public PickupPointSpawner(Itemcase itemcase) {
        this.itemcase = itemcase;
    }

    @Override
    public void run() {
        if (this.itemcase.getItem().isDead()) {
            if (this.itemcase.isInfinite()) {
                this.itemcase.spawnItem();
                this.itemcase.setWaitingForSpawn(false);
            } else {
                if (InventoryUtils.getAmountOf(this.itemcase.getInventory(),
                        this.itemcase.getItemStack()) >= 1) {
                    this.itemcase.spawnItem();
                    this.itemcase.setWaitingForSpawn(false);

                    this.itemcase.getInventory().removeItem(this.itemcase.
                            getItemStack().clone());
                } else {
                    Bukkit.getScheduler().runTaskLater(ItemCase.
                            getInstance(), new PickupPointSpawner(itemcase),
                            60);
                }
            }
        }
    }
}
