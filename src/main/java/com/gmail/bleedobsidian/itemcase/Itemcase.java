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

import com.gmail.bleedobsidian.itemcase.command.ICCommandExecutor;
import com.gmail.bleedobsidian.itemcase.configurations.ConfigFile;
import com.gmail.bleedobsidian.itemcase.listeners.BlockListener;
import com.gmail.bleedobsidian.itemcase.listeners.InventoryListener;
import com.gmail.bleedobsidian.itemcase.listeners.PlayerListener;
import com.gmail.bleedobsidian.itemcase.listeners.WorldListener;
import com.gmail.bleedobsidian.itemcase.loggers.PluginLogger;
import com.gmail.bleedobsidian.itemcase.managers.InputManager;
import com.gmail.bleedobsidian.itemcase.managers.InventoryManager;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import com.gmail.bleedobsidian.itemcase.managers.SelectionManager;
import com.gmail.bleedobsidian.itemcase.managers.ShopManager;
import com.gmail.bleedobsidian.itemcase.managers.WorldManager;
import com.gmail.bleedobsidian.itemcase.util.metrics.Graphs;
import com.gmail.bleedobsidian.itemcase.util.metrics.Metrics;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main ItemCase plugin.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ItemCase extends JavaPlugin {

    /**
     * Running instance of ItemCase.
     */
    private static ItemCase instance;

    /**
     * Config file for ItemCase.
     */
    private ConfigFile config;

    /**
     * Metrics instance.
     */
    private Metrics metrics;

    /**
     * WorldManager.
     */
    private WorldManager worldManager;

    /**
     * ItemcaseManager.
     */
    private ItemcaseManager itemcaseManager;

    /**
     * SelectionManager.
     */
    private SelectionManager selectionManager;

    /**
     * InputManager.
     */
    private InputManager inputManager;

    /**
     * ShopManager.
     */
    private ShopManager shopManager;

    /**
     * InventoryManager.
     */
    private InventoryManager inventoryManager;

    /**
     * ItemCaseAPI.
     */
    private ItemCaseAPI api;

    @Override
    public void onEnable() {
        // Set Instance
        ItemCase.instance = this;

        // Setup Logger
        PluginLogger.setJavaPlugin(this);

        // Create WorldManager
        this.worldManager = new WorldManager(this);

        // Load Configuration Files
        try {
            this.config = new ConfigFile("config.yml");
            this.config.load(this);

            // Load all world save files
            this.worldManager.load(this);
        } catch (IOException e) {
            PluginLogger.error("Failed to load configuration file.", e);
            return;
        }

        // Set language
        String locale = this.config.getFileConfiguration().getString("Locale");

        if (Language.isValid(locale)) {
            Language.setLangauge(locale + ".yml", this);
        } else {
            PluginLogger.warning("Failed to find locale: " + locale
                    + ", using en-us instead.", true);
            Language.setLangauge("en-us.yml", this);
        }

        // Check correct version of craftbukkit is being used
        try {
            Class.forName("org.bukkit.craftbukkit.v1_7_R3.CraftServer");
        } catch (ClassNotFoundException e) {
            PluginLogger.warning(
                    Language.getLanguageFile().getMessage(
                            "Console.Incorrect-Craftbukkit-Version"),
                    true);
            PluginLogger.warning(
                    Language.getLanguageFile().getMessage(
                            "Console.Disabled",
                            new String[]{"%Version%", this.getVersion()}),
                    true);
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // Set API
        this.api = new ItemCaseAPI();

        // / Check For Update
        if (this.config.getFileConfiguration().getBoolean(
                "Updates.Check-For-Update")) {
            String apiKey = this.config.getFileConfiguration().getString(
                    "Updates.API-Key");
            Updater updater;

            if (apiKey != null) {
                if (apiKey.equals("")
                        || apiKey.equalsIgnoreCase("your_api_key")) {
                    updater = new Updater(this, 68764);
                } else {
                    updater = new Updater(this, 68764, apiKey);
                }
            } else {
                updater = new Updater(this, 68764);
            }

            if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                PluginLogger.warning(Language.getLanguageFile().getMessage(
                        "Console.Update-Available"));
            } else if (updater.getResult() != Updater.UpdateResult.NO_UPDATE) {
                if (updater.getResult() == Updater.UpdateResult.ERROR_APIKEY) {
                    PluginLogger.warning("Failed to check for updates!", true);
                    PluginLogger.warning("Your API key may be wrong.", true);
                } else if (updater.getResult() == Updater.UpdateResult.ERROR_SERVER) {
                    PluginLogger.warning("Failed to check for updates!", true);
                    PluginLogger.warning("Failed to connect to server!", true);
                } else if (updater.getResult() == Updater.UpdateResult.ERROR_VERSION) {
                    PluginLogger.warning("Failed to check for updates!", true);
                    PluginLogger.warning("Invalid data in output!", true);
                }
            }
        }

        // Load Vault
        if (Vault.load(this)) {
            PluginLogger.info(Language.getLanguageFile().getMessage(
                    "Console.Vault.Successful"));
        } else {
            PluginLogger.warning(
                    Language.getLanguageFile().getMessage(
                            "Console.Vault.Unsuccessful"));
        }

        // Metrics
        try {
            this.metrics = new Metrics(this);

            if (!metrics.isOptOut()) {
                Graphs graphs = new Graphs(metrics, this.config);

                graphs.createGraphs();

                if (metrics.start()) {
                    PluginLogger.info(Language.getLanguageFile().getMessage(
                            "Console.Metrics.Successful"));
                } else {
                    PluginLogger.warning(
                            Language.getLanguageFile().getMessage(
                                    "Console.Metrics.Unsuccessful"), true);
                }
            } else {
                PluginLogger.warning(Language.getLanguageFile().getMessage(
                        "Console.Metrics.Disabled"));
            }
        } catch (IOException e) {
            PluginLogger.warning(
                    Language.getLanguageFile().getMessage(
                            "Console.Metrics.Unsuccessful"), true);
        }

        // Load WorldGuard
        if (WorldGuard.load(this)) {
            PluginLogger.info(Language.getLanguageFile().getMessage(
                    "Console.WorldGuard.Successful"));
        }

        // Create ItemcaseManager
        this.itemcaseManager = new ItemcaseManager();

        // Create SelectionManager
        this.selectionManager = new SelectionManager();

        // Create InputManager
        this.inputManager = new InputManager();

        // Create ShopManager
        this.shopManager = new ShopManager();

        // Create InventoryManager
        this.inventoryManager = new InventoryManager();

        // Register Events
        this.registerEvents();

        // Load itemcases
        this.itemcaseManager.loadItemcases();
        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Itemcases-Created"));

        // Register command
        this.getCommand("itemc").setExecutor(new ICCommandExecutor());

        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Enabled",
                new String[]{"%Version%", this.getVersion()}));
    }

    @Override
    public void onDisable() {
        // Cancel ItemcaseWhatcher
        this.getServer().getScheduler().cancelTasks(this);

        if (this.itemcaseManager != null) {
            this.itemcaseManager.unloadItemcases();
            PluginLogger.info(Language.getLanguageFile().getMessage(
                    "Console.Itemcases-Destroyed"));
        }

        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Disabled",
                new String[]{"%Version%", this.getVersion()}));
    }

    /**
     * Register event listeners.
     */
    private void registerEvents() {
        this.getServer().getPluginManager()
                .registerEvents(new BlockListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new WorldListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new InventoryListener(), this);
    }

    /**
     * @return Running instance of ItemCase.
     */
    public static ItemCase getInstance() {
        return ItemCase.instance;
    }

    /**
     * @return ItemCase API.
     */
    public ItemCaseAPI getAPI() {
        return this.api;
    }

    /**
     * @return Version of ItemCase.
     */
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    /**
     * @return ItemCase Configuration File.
     */
    public ConfigFile getConfigFile() {
        return this.config;
    }

    /**
     * @return WorldManager.
     */
    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    /**
     * @return ItemcaseManager.
     */
    public ItemcaseManager getItemcaseManager() {
        return this.itemcaseManager;
    }

    /**
     * @return SelectionManager.
     */
    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    /**
     * @return InputManager.
     */
    public InputManager getInputManager() {
        return this.inputManager;
    }

    /**
     * @return ShopManager.
     */
    public ShopManager getShopManager() {
        return this.shopManager;
    }

    /**
     * @return InventoryManager.
     */
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
}
