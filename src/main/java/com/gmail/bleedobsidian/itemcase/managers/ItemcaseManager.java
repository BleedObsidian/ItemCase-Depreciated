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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.configurations.WorldFile;
import com.gmail.bleedobsidian.itemcase.events.ItemcaseCreateEvent;
import com.gmail.bleedobsidian.itemcase.events.ItemcaseDestroyEvent;
import com.gmail.bleedobsidian.itemcase.loggers.PluginLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;
import com.gmail.bleedobsidian.itemcase.tasks.ItemcaseWatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A manager to handle all itemcases. (Only used internally, please use the API
 * instead)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemcaseManager {

    /**
     * ItemcaseWatcher.
     */
    private final ItemcaseWatcher itemcaseWatcher;

    /**
     * Loaded Itemcases.
     */
    private final List<Itemcase> itemcases = new ArrayList<Itemcase>();

    /**
     * New ItemcaseManager.
     */
    public ItemcaseManager() {
        this.itemcaseWatcher = new ItemcaseWatcher();
        ItemCase.getInstance().getServer().getScheduler()
                .scheduleSyncRepeatingTask(ItemCase.getInstance(),
                        itemcaseWatcher, 10, 40);
    }

    /**
     * Create new Itemcase.
     *
     * @param itemStack ItemStack.
     * @param blockLocation Bukkit block location.
     * @param player Player.
     * @return Itemcase.
     */
    public Itemcase createItemcase(ItemStack itemStack, Location blockLocation,
            Player player) {
        World world = player.getWorld();
        WorldFile saveFile = ItemCase.getInstance().getWorldManager().
                getWorldFile(world);

        if (saveFile != null) {
            Itemcase itemcase = new Itemcase(itemStack, blockLocation,
                    player.getName());

            ItemcaseCreateEvent event = new ItemcaseCreateEvent(itemcase,
                    player);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return null;
            }

            this.itemcases.add(itemcase);
            itemcase.spawnItem();

            String path = "Itemcases." + blockLocation.getBlockX() + "/"
                    + blockLocation.getBlockY() + "/"
                    + blockLocation.getBlockZ();

            saveFile.getConfigFile().getFileConfiguration()
                    .set(path + ".Owner", player.getUniqueId().toString());

            saveFile.getConfigFile().getFileConfiguration()
                    .set(path + ".Item", itemStack.serialize());

            saveFile.getConfigFile().getFileConfiguration()
                    .set(path + ".Type", "SHOWCASE");

            saveFile.getConfigFile().getFileConfiguration()
                    .set(path + ".Infinite", false);

            itemcase.setInventory(Bukkit.createInventory(null, 54,
                    "ItemCase Storage"));

            saveFile.getConfigFile()
                    .getFileConfiguration()
                    .set(path + ".Inventory",
                            this.serializeInventory(itemcase.getInventory()));

            saveFile.getConfigFile().save(ItemCase.getInstance());

            return itemcase;
        } else {
            return null;
        }
    }

    /**
     * Destroy Itemcase.
     *
     * @param itemcase Itemcase.
     * @param player Player that caused event (Can be null)
     * @return If successful.
     */
    public boolean destroyItemcase(Itemcase itemcase, Player player) {
        WorldFile saveFile = ItemCase.getInstance().getWorldManager().
                getWorldFile(itemcase.getBlock()
                        .getWorld());

        if (saveFile != null) {
            ItemcaseDestroyEvent event = new ItemcaseDestroyEvent(itemcase,
                    player);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            itemcase.despawnItem();
            this.itemcases.remove(itemcase);

            String path = "Itemcases."
                    + itemcase.getBlock().getLocation().getBlockX() + "/"
                    + itemcase.getBlock().getLocation().getBlockY() + "/"
                    + itemcase.getBlock().getLocation().getBlockZ();

            saveFile.getConfigFile().getFileConfiguration().set(path, null);
            saveFile.getConfigFile().save(ItemCase.getInstance());

            if (itemcase.getInventory() != null) {
                for (ItemStack itemStack : itemcase.getInventory().getContents()) {
                    if (itemStack != null) {
                        itemcase.getBlock()
                                .getWorld()
                                .dropItemNaturally(
                                        itemcase.getBlock().getLocation(),
                                        itemStack);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Save Itemcase into config.
     *
     * @param itemcase Itemcase.
     */
    public void saveItemcase(Itemcase itemcase) {
        World world = itemcase.getBlock().getWorld();
        WorldFile saveFile = ItemCase.getInstance().getWorldManager().
                getWorldFile(world);

        if (saveFile != null) {
            String path = "Itemcases."
                    + itemcase.getBlock().getLocation().getBlockX() + "/"
                    + itemcase.getBlock().getLocation().getBlockY() + "/"
                    + itemcase.getBlock().getLocation().getBlockZ();

            saveFile.getConfigFile().getFileConfiguration()
                    .set(path + ".Infinite", itemcase.isInfinite());

            if (itemcase.getType() == ItemcaseType.SHOWCASE) {
                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Type", "SHOWCASE");

                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Shop", null);
            } else if (itemcase.getType() == ItemcaseType.SHOP) {
                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Type", "SHOP");

                if (itemcase.canBuy()) {
                    saveFile.getConfigFile().getFileConfiguration()
                            .set(path + ".Shop.Buy", true);
                } else {
                    saveFile.getConfigFile().getFileConfiguration()
                            .set(path + ".Shop.Buy", false);
                }

                if (itemcase.canSell()) {
                    saveFile.getConfigFile().getFileConfiguration()
                            .set(path + ".Shop.Sell", true);
                } else {
                    saveFile.getConfigFile().getFileConfiguration()
                            .set(path + ".Shop.Sell", false);
                }

                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Shop.BuyPrice", itemcase.getBuyPrice());
                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Shop.SellPrice", itemcase.getSellPrice());
            } else if (itemcase.getType() == ItemcaseType.PICKUP_POINT) {
                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Type", "PICKUP_POINT");

                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".PickupPoint.Interval", itemcase.
                                getPickupPointInterval());

                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Shop", null);
            }

            if (itemcase.getInventory() != null) {
                saveFile.getConfigFile()
                        .getFileConfiguration()
                        .set(path + ".Inventory",
                                this.serializeInventory(itemcase
                                        .getInventory()));
            } else {
                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Inventory", null);
            }

            saveFile.getConfigFile().save(ItemCase.getInstance());
        }
    }

    /**
     * Load all saved Itemcases and create them.
     */
    public void loadItemcases() {
        for (World world : ItemCase.getInstance().getWorldManager().getWorlds()) {
            WorldFile saveFile = ItemCase.getInstance().getWorldManager().
                    getWorldFile(world);

            if (saveFile.getConfigFile().getFileConfiguration() != null) {
                if (saveFile.getConfigFile().getFileConfiguration()
                        .getConfigurationSection("Itemcases") != null) {

                    Set<String> set = saveFile.getConfigFile()
                            .getFileConfiguration()
                            .getConfigurationSection("Itemcases")
                            .getKeys(false);

                    for (String name : set) {
                        String path = "Itemcases." + name;

                        String[] splitStringLocation = name.split("/");

                        Double[] locations = new Double[3];

                        try {
                            locations[0] = Double
                                    .parseDouble(splitStringLocation[0]);
                            locations[1] = Double
                                    .parseDouble(splitStringLocation[1]);
                            locations[2] = Double
                                    .parseDouble(splitStringLocation[2]);
                        } catch (NumberFormatException e) {
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Load-Error"),
                                            true);
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Parse-Error"),
                                            true);
                            continue;
                        }

                        Location location = new Location(world, locations[0],
                                locations[1], locations[2]);

                        if (!ItemCase
                                .getInstance()
                                .getConfigFile()
                                .getFileConfiguration()
                                .getIntegerList("Blocks")
                                .contains(location.getBlock().getType().getId())) {
                            location.getBlock().setType(Material.STEP);
                        }

                        String ownerString = saveFile.getConfigFile()
                                .getFileConfiguration()
                                .getString(path + ".Owner");

                        if (ownerString == null) {
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Load-Error"),
                                            true);
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Owner-Error"),
                                            true);
                            continue;
                        }

                        String playerName;
                        try {
                            playerName = Bukkit.getOfflinePlayer(
                                    UUID.fromString(ownerString)).getName();
                        } catch (IllegalArgumentException e) {
                            playerName = ownerString;

                            saveFile.getConfigFile()
                                    .getFileConfiguration()
                                    .set(path + ".Owner",
                                            Bukkit.getOfflinePlayer(ownerString)
                                            .getUniqueId().toString());
                            saveFile.getConfigFile().
                                    save(ItemCase.getInstance());
                        }

                        Map<String, Object> itemValues;
                        try {
                            itemValues = saveFile.getConfigFile()
                                    .getFileConfiguration()
                                    .getConfigurationSection(path + ".Item")
                                    .getValues(true);
                        } catch (NullPointerException e) {
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Load-Error"),
                                            true);
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Item-Error"),
                                            true);
                            continue;
                        }

                        ItemStack item = ItemStack.deserialize(itemValues);

                        Itemcase itemcase = new Itemcase(item, location,
                                playerName);

                        if (saveFile.getConfigFile().getFileConfiguration()
                                .getString(path + ".Type").equals("SHOWCASE")) {
                            itemcase.setType(ItemcaseType.SHOWCASE);
                        } else if (saveFile.getConfigFile()
                                .getFileConfiguration()
                                .getString(path + ".Type").equals("SHOP")) {
                            itemcase.setType(ItemcaseType.SHOP);

                            if (saveFile.getConfigFile().getFileConfiguration()
                                    .getBoolean(path + ".Shop.Buy")) {
                                itemcase.setCanBuy(true);
                                itemcase.setBuyPrice(saveFile.getConfigFile()
                                        .getFileConfiguration()
                                        .getDouble(path + ".Shop.BuyPrice"));
                            }

                            if (saveFile.getConfigFile().getFileConfiguration()
                                    .getBoolean(path + ".Shop.Sell")) {
                                itemcase.setCanSell(true);
                                itemcase.setSellPrice(saveFile.getConfigFile()
                                        .getFileConfiguration()
                                        .getDouble(path + ".Shop.SellPrice"));
                            }
                        } else if (saveFile.getConfigFile()
                                .getFileConfiguration()
                                .getString(path + ".Type").
                                equals("PICKUP_POINT")) {
                            itemcase.setPickupPointInterval(saveFile.
                                    getConfigFile()
                                    .getFileConfiguration()
                                    .getInt(path + ".PickupPoint.Interval"));
                            itemcase.setType(ItemcaseType.PICKUP_POINT);
                        } else {
                            PluginLogger
                                    .warning(
                                            Language.getLanguageFile()
                                            .getMessage(
                                                    "Console.Errors.Itemcase-Loader.Load-Error"),
                                            true);
                        }

                        itemcase.setInfinite(saveFile.getConfigFile()
                                .getFileConfiguration()
                                .getBoolean(path + ".Infinite"));

                        if (!itemcase.isInfinite()) {
                            Inventory inventory = this.deserializeInventory(
                                    saveFile, path + ".Inventory", 54);
                            itemcase.setInventory(inventory);
                        }

                        this.itemcases.add(itemcase);
                        itemcase.spawnItem();
                    }
                }
            }
        }
    }

    /**
     * Unload all Itemcases.
     */
    public void unloadItemcases() {
        List<Itemcase> itemcases = new ArrayList<Itemcase>(this.itemcases);

        for (Itemcase itemcase : itemcases) {
            itemcase.despawnItem();
            this.itemcases.remove(itemcase);
        }
    }

    /**
     * @return List of all created Itemcases.
     */
    public List<Itemcase> getItemcases() {
        return this.itemcases;
    }

    /**
     * @param blockLocation Bukkit block location.
     * @return If Itemcase is at given location.
     */
    public boolean isItemcaseAt(Location blockLocation) {
        for (Itemcase itemcase : this.itemcases) {
            if (itemcase.getBlock().getLocation().equals(blockLocation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param blockLocation Bukkit block.
     * @return Get Itemcase at given location. (Null if doesn't exist)
     */
    public Itemcase getItemcaseAt(Location blockLocation) {
        for (Itemcase itemcase : this.itemcases) {
            if (itemcase.getBlock().getLocation().equals(blockLocation)) {
                return itemcase;
            }
        }

        return null;
    }

    /**
     * @param inventory Serialize given inventory.
     * @return Serialized map value.
     */
    public Map<String, Object> serializeInventory(Inventory inventory) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                map.put("" + i, inventory.getItem(i).serialize());
            }
        }

        return map;
    }

    /**
     * @param config WorldFile.
     * @param path Path.
     * @param size Size.
     * @return Inventory.
     */
    public Inventory deserializeInventory(WorldFile config, String path,
            int size) {
        Map<String, Object> map = config.getConfigFile().getFileConfiguration()
                .getConfigurationSection(path).getValues(false);
        Inventory inventory = Bukkit.createInventory(null, size,
                "ItemCase Storage");

        for (Entry<String, Object> entry : map.entrySet()) {
            inventory.setItem(
                    Integer.parseInt(entry.getKey()),
                    ItemStack.deserialize(config
                            .getConfigFile()
                            .getFileConfiguration()
                            .getConfigurationSection(
                                    path + "." + entry.getKey())
                            .getValues(true)));
        }

        return inventory;
    }
}
