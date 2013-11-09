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
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Itemcase {
    private Item item;
    private ItemStack itemStack;
    private Location blockLocation;
    private String player;

    private boolean isChunkLoaded;

    public Itemcase(ItemStack itemStack, Location blockLocation, String player) {
        this.itemStack = itemStack;
        this.blockLocation = blockLocation;
        this.player = player;

        this.setChunkLoaded(blockLocation.getWorld().isChunkLoaded(
                blockLocation.getChunk()));
    }

    public void spawnItem() {
        if (this.isChunkLoaded) {
            Location itemLocation = new Location(blockLocation.getWorld(),
                    blockLocation.getBlockX() + 0.5,
                    blockLocation.getBlockY() + 1.0,
                    blockLocation.getBlockZ() + 0.5);

            this.item = blockLocation.getWorld().dropItem(itemLocation,
                    this.itemStack);
            this.item.setVelocity(new Vector(0.0, 0.1, 0.0));
        }
    }

    public void despawnItem() {
        if (!item.isDead()) {
            this.item.remove();
        }
    }

    public boolean isChunkLoaded() {
        return this.isChunkLoaded;
    }

    public void setChunkLoaded(boolean bool) {
        this.isChunkLoaded = bool;

        if (bool) {
            this.spawnItem();
        } else {
            this.despawnItem();
        }
    }

    public Item getItem() {
        return this.item;
    }

    public Block getBlock() {
        return this.blockLocation.getBlock();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public String getPlayerName() {
        return this.player;
    }
}
