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

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.bleedobsidian.itemcase.command.ICCommandExecutor;
import com.gmail.bleedobsidian.itemcase.configurations.ConfigFile;
import com.gmail.bleedobsidian.itemcase.listeners.BlockListener;
import com.gmail.bleedobsidian.itemcase.listeners.PlayerListener;
import com.gmail.bleedobsidian.itemcase.listeners.WorldListener;
import com.gmail.bleedobsidian.itemcase.loggers.PluginLogger;
import com.gmail.bleedobsidian.itemcase.managers.AmountManager;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import com.gmail.bleedobsidian.itemcase.managers.SelectionManager;
import com.gmail.bleedobsidian.itemcase.managers.ShopManager;
import com.gmail.bleedobsidian.itemcase.managers.WorldManager;

public class ItemCase extends JavaPlugin {
    private ConfigFile config;

    private WorldManager worldManager;
    private ItemcaseManager itemcaseManager;
    private SelectionManager selectionManager;
    private AmountManager amountManager;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
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

        if (Language.exists(locale)) {
            Language.setLangauge(locale + ".yml", this);
        } else {
            PluginLogger.warning("Failed to find locale: " + locale
                    + ", using en-us instead.", true);
            Language.setLangauge("en-us.yml", this);
        }

        // / Check For Update
        if (this.config.getFileConfiguration().getBoolean(
                "Updates.Check-For-Update")) {
            String apiKey = this.config.getFileConfiguration().getString(
                    "Updates.API-Key");
            Updater updater;

            if (apiKey != null) {
                if (apiKey.equals("")
                        || apiKey.equalsIgnoreCase("your_api_key")) {
                    updater = new Updater(this, 0); // TODO: Project ID.
                } else {
                    updater = new Updater(this, 0, apiKey); // TODO: Project ID.
                }
            } else {
                updater = new Updater(this, 0); // TODO: Project ID.
            }

            if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                PluginLogger
                        .warning("A new version of AreaProtect is available!");
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
        if (!Vault.load(this)) {
            PluginLogger.error("Failed to load Vault.");
            return;
        }

        // Create ItemcaseManager
        this.itemcaseManager = new ItemcaseManager(this, this.worldManager);

        // Create SelectionManager
        this.selectionManager = new SelectionManager();

        // Create AmountManager
        this.amountManager = new AmountManager(this);

        // Create ShopManager
        this.shopManager = new ShopManager(this);

        // Register Events
        this.registerEvents();

        // Load itemcases
        this.itemcaseManager.loadItemcases();
        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Itemcases-Created"));

        // Register command
        this.getCommand("ic").setExecutor(new ICCommandExecutor(this));

        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Enabled",
                new String[] { "%Version%", this.getVersion() }));
    }

    @Override
    public void onDisable() {
        // Cancel ItemcaseWhatcher
        this.getServer().getScheduler().cancelTasks(this);

        this.itemcaseManager.unloadItemcases();
        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Itemcases-Destroyed"));

        PluginLogger.info(Language.getLanguageFile().getMessage(
                "Console.Disabled",
                new String[] { "%Version%", this.getVersion() }));
    }

    private void registerEvents() {
        this.getServer().getPluginManager()
                .registerEvents(new BlockListener(this), this);
        this.getServer().getPluginManager()
                .registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager()
                .registerEvents(new WorldListener(this), this);
    }

    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    public ItemcaseManager getItemcaseManager() {
        return this.itemcaseManager;
    }

    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    public AmountManager getAmountManager() {
        return this.amountManager;
    }

    public ShopManager getShopManager() {
        return this.shopManager;
    }
}
