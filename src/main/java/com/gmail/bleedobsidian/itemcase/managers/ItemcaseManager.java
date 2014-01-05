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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.configurations.WorldFile;
import com.gmail.bleedobsidian.itemcase.loggers.PluginLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;
import com.gmail.bleedobsidian.itemcase.tasks.ItemcaseWatcher;

public class ItemcaseManager {
    private ItemCase plugin;
    private WorldManager worldManager;

    private ItemcaseWatcher itemcaseWatcher;

    private List<Itemcase> itemcases = new ArrayList<Itemcase>();

    public ItemcaseManager(ItemCase plugin, WorldManager worldManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;

        this.itemcaseWatcher = new ItemcaseWatcher(this);
        plugin.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, itemcaseWatcher, 10, 40);
    }

    public void createItemcase(ItemStack itemStack, Location blockLocation,
            Player player) {
        World world = player.getWorld();
        WorldFile saveFile = this.worldManager.getWorldFile(world);

        if (saveFile != null) {
            Itemcase itemcase = new Itemcase(itemStack, blockLocation,
                    player.getName());

            this.itemcases.add(itemcase);

            String path = "Itemcases." + blockLocation.getBlockX() + "/"
                    + blockLocation.getBlockY() + "/"
                    + blockLocation.getBlockZ();

            saveFile.getConfigFile().getFileConfiguration()
                    .set(path + ".Owner", player.getName());

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

            saveFile.getConfigFile().save(plugin);
        }
    }

    public void destroyItemcase(Itemcase itemcase) {
        WorldFile saveFile = this.worldManager.getWorldFile(itemcase.getBlock()
                .getWorld());

        if (saveFile != null) {
            itemcase.despawnItem();
            this.itemcases.remove(itemcase);

            String path = "Itemcases."
                    + itemcase.getBlock().getLocation().getBlockX() + "/"
                    + itemcase.getBlock().getLocation().getBlockY() + "/"
                    + itemcase.getBlock().getLocation().getBlockZ();

            saveFile.getConfigFile().getFileConfiguration().set(path, null);
            saveFile.getConfigFile().save(this.plugin);
        }
    }

    public void saveItemcase(Itemcase itemcase) {
        World world = itemcase.getBlock().getWorld();
        WorldFile saveFile = this.worldManager.getWorldFile(world);

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

                if (!itemcase.isInfinite()) {
                    saveFile.getConfigFile()
                            .getFileConfiguration()
                            .set(path + ".Inventory",
                                    this.serializeInventory(itemcase
                                            .getInventory()));
                } else {
                    saveFile.getConfigFile().getFileConfiguration()
                            .set(path + ".Inventory", null);
                }

                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Shop.BuyPrice", itemcase.getBuyPrice());
                saveFile.getConfigFile().getFileConfiguration()
                        .set(path + ".Shop.SellPrice", itemcase.getSellPrice());
            }

            saveFile.getConfigFile().save(plugin);
        }
    }

    public void loadItemcases() {
        for (World world : this.worldManager.getWorlds()) {
            WorldFile saveFile = this.worldManager.getWorldFile(world);

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

                        if (!location.getBlock().getType()
                                .equals(Material.STEP)
                                && !location.getBlock().getType()
                                        .equals(Material.WOOD_STEP)) {
                            location.getBlock().setType(Material.STEP);
                        }

                        String playerName = saveFile.getConfigFile()
                                .getFileConfiguration()
                                .getString(path + ".Owner");

                        if (playerName == null) {
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
                    }
                }
            }
        }
    }

    public void unloadItemcases() {
        List<Itemcase> itemcases = new ArrayList<Itemcase>(this.itemcases);

        for (Itemcase itemcase : itemcases) {
            itemcase.despawnItem();
            this.itemcases.remove(itemcase);
        }
    }

    public List<Itemcase> getItemcases() {
        return this.itemcases;
    }

    public boolean isItemcaseAt(Location blockLocation) {
        for (Itemcase itemcase : this.itemcases) {
            if (itemcase.getBlock().getLocation().equals(blockLocation)) {
                return true;
            }
        }

        return false;
    }

    public Itemcase getItemcaseAt(Location blockLocation) {
        for (Itemcase itemcase : this.itemcases) {
            if (itemcase.getBlock().getLocation().equals(blockLocation)) {
                return itemcase;
            }
        }

        return null;
    }

    public Map<String, Object> serializeInventory(Inventory inventory) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                map.put("" + i, inventory.getItem(i).serialize());
            }
        }

        return map;
    }

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
