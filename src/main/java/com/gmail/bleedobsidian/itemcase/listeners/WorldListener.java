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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

/**
 * World related event listener. (Only used internally)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class WorldListener implements Listener {
    private ItemCase plugin;

    /**
     * New WorldListener.
     * 
     * @param plugin
     *            - ItemCase plugin.
     */
    public WorldListener(ItemCase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Itemcase itemcase : this.plugin.getItemcaseManager()
                .getItemcases()) {
            if (event.getChunk().equals(itemcase.getBlock().getChunk())
                    && itemcase.isChunkLoaded() == false) {
                itemcase.setChunkLoaded(true);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Itemcase itemcase : this.plugin.getItemcaseManager()
                .getItemcases()) {
            if (event.getChunk().equals(itemcase.getBlock().getChunk())
                    && itemcase.isChunkLoaded() == true) {
                itemcase.setChunkLoaded(false);
            }
        }
    }
}
