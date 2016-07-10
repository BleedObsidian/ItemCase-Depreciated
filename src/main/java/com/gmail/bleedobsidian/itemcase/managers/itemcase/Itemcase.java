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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.tasks.PickupPointSpawner;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
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
public final class Itemcase {

    /**
     * Item Entity that is on display.
     */
    private Item item;

    /**
     * ItemStack used for the display.
     */
    private final ItemStack displayStack;

    /**
     * ItemStack that Itemcase is for.
     */
    private final ItemStack itemStack;

    /**
     * Location of Itemcase.
     */
    private final Location blockLocation;

    /**
     * Owner of Itemcase.
     */
    private final String player;

    /**
     * ItemcaseType.
     */
    private ItemcaseType type = ItemcaseType.SHOWCASE;

    /**
     * If players can buy from this Itemcase.
     */
    private boolean canBuy;

    /**
     * If players can sell to this Itemcase.
     */
    private boolean canSell;

    /**
     * Buy price of item.
     */
    private double buyPrice = 0;

    /**
     * Sell price of item.
     */
    private double sellPrice = 0;

    /**
     * If Itemcase is infinite.
     */
    private boolean isInfinite;

    /**
     * Inventory of Itemcase.
     */
    private Inventory inventory;

    /**
     * If chunk that Itemcase is in is loaded.
     */
    private boolean isChunkLoaded;

    /**
     * Interval in ticks between pickup point spawns.
     */
    private int pickupPointInterval = 40;

    /**
     * If the Itemcase is waiting for a pickup point spawn.
     */
    private boolean isWaitingForSpawn;

    /**
     * Create new Itemcase instance.
     *
     * @param itemStack ItemStack that Itemcase is for.
     * @param blockLocation Location of Itemcase.
     * @param player Owner of Itemcase.
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

        this.isChunkLoaded = blockLocation.getWorld().isChunkLoaded(
                blockLocation.getChunk());
    }

    /**
     * Spawn display item.
     */
    public void spawnItem() {
        if (this.isChunkLoaded) {
            Location itemLocation = new Location(blockLocation.getWorld(),
                    blockLocation.getBlockX() + 0.5,
                    blockLocation.getBlockY() + 1.5,
                    blockLocation.getBlockZ() + 0.5);

            if (this.type != ItemcaseType.PICKUP_POINT) {
                net.minecraft.server.v1_10_R1.ItemStack stack = CraftItemStack
                        .asNMSCopy(this.displayStack);
                stack.count = 0;

                this.item = blockLocation.getWorld().dropItem(itemLocation,
                        CraftItemStack.asBukkitCopy(stack));
                this.item.setVelocity(new Vector(0.0, 0.1, 0.0));
                this.item.setMetadata("ItemCase", new ItemcaseData());
            } else {
                net.minecraft.server.v1_10_R1.ItemStack stack = CraftItemStack
                        .asNMSCopy(this.displayStack);
                stack.count = 1;

                this.item = blockLocation.getWorld().dropItem(itemLocation,
                        CraftItemStack.asBukkitCopy(stack));
                this.item.setVelocity(new Vector(0.0, 0.1, 0.0));
            }

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
     * Despawn display item.
     */
    public void despawnItem() {
        if (this.item != null) {
            this.item.remove();
        }
    }

    /**
     * @return Inventory of Itemcase. (Maybe null)
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @param inventory Inventory of Itemcase.
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return If chunk that Itemcase is in is loaded.
     */
    public boolean isChunkLoaded() {
        return this.isChunkLoaded;
    }

    /**
     * @param bool If chunk that Itemcase is in is loaded.
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
     * @return Itemcase display Item entity.
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * @return Itemcase block.
     */
    public Block getBlock() {
        return this.blockLocation.getBlock();
    }

    /**
     * @return ItemStack of Itemcase.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * @return Owner name.
     */
    public String getOwnerName() {
        return this.player;
    }

    /**
     * @param type Type of Itemcase.
     */
    public void setType(ItemcaseType type) {
        ItemcaseType previousType = this.type;
        this.type = type;

        if (previousType != this.type) {
            if (this.item != null && type != ItemcaseType.PICKUP_POINT) {
                this.despawnItem();
                this.spawnItem();
            }

            if (type == ItemcaseType.PICKUP_POINT) {
                this.despawnItem();
                Bukkit.getScheduler().runTaskLater(ItemCase.
                        getInstance(), new PickupPointSpawner(
                                this),
                        60);
                this.setWaitingForSpawn(true);
            }

            if (previousType == ItemcaseType.SHOP || previousType == ItemcaseType.PICKUP_POINT) {
                if (!this.isInfinite && this.inventory != null) {
                    for (ItemStack itemStack : this.inventory.
                            getContents()) {
                        if (itemStack != null) {
                            this.blockLocation
                                    .getWorld()
                                    .dropItemNaturally(
                                            this.blockLocation,
                                            itemStack);
                        }
                    }

                    this.inventory = null;
                }
            }
        }

        if (previousType != ItemcaseType.SHOP || previousType != ItemcaseType.PICKUP_POINT) {
            if (type == ItemcaseType.SHOP || type == ItemcaseType.PICKUP_POINT) {
                this.inventory = Bukkit.createInventory(null, 54,
                        "ItemCase Storage");
            }
        }

        this.isInfinite = false;
    }

    /**
     * @return Type of Itemcase.
     */
    public ItemcaseType getType() {
        return this.type;
    }

    /**
     * @return If players can buy from this Itemcase.
     */
    public boolean canBuy() {
        return canBuy;
    }

    /**
     * @param canBuy If players can buy from this Itemcase.
     */
    public void setCanBuy(boolean canBuy) {
        this.canBuy = canBuy;
    }

    /**
     * @return If players can sell to this Itemcase.
     */
    public boolean canSell() {
        return canSell;
    }

    /**
     * @param canSell If players can sell to this Itemcase.
     */
    public void setCanSell(boolean canSell) {
        this.canSell = canSell;
    }

    /**
     * @return Buy price of item. (If can buy)
     */
    public double getBuyPrice() {
        return buyPrice;
    }

    /**
     * @param buyPrice Buy price of item. (If can buy)
     */
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * @return Sell price of item. (If can sell)
     */
    public double getSellPrice() {
        return sellPrice;
    }

    /**
     * @param sellPrice Sell price of item. (If can sell)
     */
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    /**
     * @return If Itemcase is infinite.
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * @param isInfinite If Itemcase is infinite.
     */
    public void setInfinite(boolean isInfinite) {
        boolean previousIsInfinite = this.isInfinite;
        this.isInfinite = isInfinite;

        if (!previousIsInfinite) {
            if (this.isInfinite) {
                if (this.inventory != null) {
                    for (ItemStack itemStack : this.inventory.
                            getContents()) {
                        if (itemStack != null) {
                            this.blockLocation
                                    .getWorld()
                                    .dropItemNaturally(
                                            this.blockLocation,
                                            itemStack);
                        }
                    }

                    this.inventory = null;
                }
            }
        }

        if (!isInfinite) {
            if (previousIsInfinite != false) {
                this.inventory = Bukkit.createInventory(null, 54,
                        "ItemCase Storage");
            }
        }
    }

    /**
     * @param ticks Ticks between pickup point spawns.
     */
    public void setPickupPointInterval(int ticks) {
        this.pickupPointInterval = ticks;
    }

    /**
     * @return Ticks between pickup point spawns.
     */
    public int getPickupPointInterval() {
        return this.pickupPointInterval;
    }

    /**
     * @param isWaitingForSpawn If Itemcase is waiting for a pickup point spawn.
     */
    public void setWaitingForSpawn(boolean isWaitingForSpawn) {
        this.isWaitingForSpawn = isWaitingForSpawn;
    }

    /**
     * @return If Itemcase is waiting for a pickup point spawn.
     */
    public boolean isWaitingForSpawn() {
        return this.isWaitingForSpawn;
    }
}
