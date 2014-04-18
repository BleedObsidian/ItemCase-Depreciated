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

package com.gmail.bleedobsidian.itemcase.managers.itemcase;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * Itemcase handler.
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class Itemcase {
    private Item item;
    private ItemStack displayStack;
    private ItemStack itemStack;
    private Location blockLocation;
    private String player;

    private ItemcaseType type = ItemcaseType.SHOWCASE;
    private boolean canBuy;
    private boolean canSell;

    private double buyPrice = 0;
    private double sellPrice = 0;

    private boolean isInfinite;
    private Inventory inventory;

    private boolean isChunkLoaded;

    /**
     * Create new itemcase instance.
     * 
     * @param itemStack
     *            - ItemStack.
     * @param blockLocation
     *            - Bukkit block location.
     * @param player
     *            - Player creator.
     */
    public Itemcase(ItemStack itemStack, Location blockLocation, String player) {
        this.itemStack = itemStack;

        this.displayStack = itemStack.clone();
        ItemMeta meta = displayStack.getItemMeta();
        meta.setDisplayName(UUID.randomUUID().toString()); // Stop item
                                                           // stacking.
        this.displayStack.setItemMeta(meta);

        this.blockLocation = blockLocation;
        this.player = player;

        this.setChunkLoaded(blockLocation.getWorld().isChunkLoaded(
                blockLocation.getChunk()));
    }

    /**
     * Spawn item on slab.
     */
    public void spawnItem() {
        if (this.isChunkLoaded) {
            Location itemLocation = new Location(blockLocation.getWorld(),
                    blockLocation.getBlockX() + 0.5,
                    blockLocation.getBlockY() + 1.5,
                    blockLocation.getBlockZ() + 0.5);

            net.minecraft.server.v1_7_R3.ItemStack stack = CraftItemStack
                    .asNMSCopy(this.displayStack);
            stack.count = 0;

            this.item = blockLocation.getWorld().dropItem(itemLocation,
                    CraftItemStack.asBukkitCopy(stack));
            this.item.setVelocity(new Vector(0.0, 0.1, 0.0));
            this.item.setMetadata("ItemCase", new ItemcaseData());

            Chunk chunk = blockLocation.getChunk();

            for (Entity entity : chunk.getEntities()) {
                if (entity.getLocation().getBlock().getLocation()
                        .equals(this.blockLocation)
                        && entity instanceof Item && !entity.equals(this.item)) {
                    entity.remove();
                }
            }
        }
    }

    /**
     * Despawn item on slab.
     */
    public void despawnItem() {
        this.item.remove();
    }

    /**
     * @return - Inventory of Itemcase. (Maybe null)
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @param inventory
     *            - Inventory of Itemcase.
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return - If Itemcase chunk is loaded.
     */
    public boolean isChunkLoaded() {
        return this.isChunkLoaded;
    }

    /**
     * @param bool
     *            - If itemcase chunk is loaded.
     */
    public void setChunkLoaded(boolean bool) {
        this.isChunkLoaded = bool;

        if (bool) {
            this.spawnItem();
        } else {
            this.despawnItem();
        }
    }

    /**
     * @return - Itemcase item entity.
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * @return - Itemcase block.
     */
    public Block getBlock() {
        return this.blockLocation.getBlock();
    }

    /**
     * @return - ItemStack of Itemcase.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * @return - Player name of owner.
     */
    public String getOwnerName() {
        return this.player;
    }

    /**
     * @param type
     *            - Type of Itemcase.
     */
    public void setType(ItemcaseType type) {
        this.type = type;
    }

    /**
     * @return - Type of itemcase.
     */
    public ItemcaseType getType() {
        return this.type;
    }

    /**
     * @return - If players can buy from the itemcase.
     */
    public boolean canBuy() {
        return canBuy;
    }

    /**
     * @param canBuy
     *            - If players can buy from the itemcase.
     */
    public void setCanBuy(boolean canBuy) {
        this.canBuy = canBuy;
    }

    /**
     * @return - If players can sell to the itemcase.
     */
    public boolean canSell() {
        return canSell;
    }

    /**
     * @param canSell
     *            - If players can sell to the itemcase.
     */
    public void setCanSell(boolean canSell) {
        this.canSell = canSell;
    }

    /**
     * @return - Buy price of item. (If can buy)
     */
    public double getBuyPrice() {
        return buyPrice;
    }

    /**
     * @param buyPrice
     *            - Buy price of item. (If can buy)
     */
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * @return - Sell price of item. (If can sell)
     */
    public double getSellPrice() {
        return sellPrice;
    }

    /**
     * @param sellPrice
     *            - Sell price of item. (If can sell)
     */
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    /**
     * @return - If itemcase is infinite.
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * @param isInfinite
     *            - If itemcase is infinite.
     */
    public void setInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }
}
