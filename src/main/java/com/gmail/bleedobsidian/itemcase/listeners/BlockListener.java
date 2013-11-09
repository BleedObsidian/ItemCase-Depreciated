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

package com.gmail.bleedobsidian.itemcase.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.bleedobsidian.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;

public class BlockListener implements Listener {
    private ItemcaseManager itemcaseManager;

    public BlockListener(ItemcaseManager itemcaseManager) {
        this.itemcaseManager = itemcaseManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            List<Itemcase> itemcases = new ArrayList<Itemcase>(
                    this.itemcaseManager.getItemcases());

            for (Itemcase itemcase : itemcases) {
                if (event.getBlock().equals(itemcase.getBlock())) {
                    this.itemcaseManager.destroyItemcase(itemcase);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlockAgainst();

            if (this.itemcaseManager.isItemcaseCreatedAt(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
