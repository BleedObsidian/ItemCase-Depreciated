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
package com.gmail.bleedobsidian.itemcase.configurations;

import com.gmail.bleedobsidian.itemcase.loggers.PluginLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class allows you to create/load a configuration file.
 *
 * @author Jesse Prescott
 */
public class ConfigFile {

    /**
     * FileConfiguration.
     */
    private FileConfiguration fileConfiguration;

    /**
     * Name of config file.
     */
    private String name;

    /**
     * Name of default config to copy from.
     */
    private String defaultName;

    /**
     * New Configuration File with given name.
     *
     * @param name Name of config file.
     */
    public ConfigFile(String name) {
        this.name = name;
        this.defaultName = name;
    }

    /**
     * New Configuration File with given name and name of default config.
     *
     * @param name Name of config file.
     * @param defaultName Name of the default config to copy.
     */
    public ConfigFile(String name, String defaultName) {
        this.name = name;
        this.defaultName = defaultName;
    }

    /**
     * Load this configuration file into memory. (Creates default version if it
     * doesn't exist)
     *
     * @param plugin JavaPlugin.
     * @throws IOException Fails to load config file.
     */
    public void load(JavaPlugin plugin) throws IOException {
        if (!(new File(plugin.getDataFolder(), this.name)).exists()) {
            this.saveDefault(plugin);
            this.fileConfiguration = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder(),
                                    this.name));
        } else {
            this.fileConfiguration = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder(),
                                    this.name));
        }
    }

    /**
     * Save default version of this configuration file to data folder.
     *
     * @param plugin JavaPlugin.
     */
    public void saveDefault(JavaPlugin plugin) {
        File outFile = new File(plugin.getDataFolder(), this.name);

        InputStream inputStream = plugin.getResource(defaultName);
        OutputStream outputStream = null;

        if (inputStream != null) {
            if (!outFile.exists()) {
                outFile.getParentFile().mkdirs();
            }

            try {
                outputStream = new FileOutputStream(outFile);

                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, len);
                }
            } catch (IOException e) {
                PluginLogger.error("Failed to copy resource.", e);
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    PluginLogger.error("Failed to close resource streams.", e);
                }
            }
        }
    }

    /**
     * Save this configuration file into data folder.
     *
     * @param plugin JavaPlugin.
     */
    public void save(JavaPlugin plugin) {
        try {
            this.fileConfiguration.save(new File(plugin.getDataFolder(),
                    this.name));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Failed to save config file " + this.name, e);
        }
    }

    /**
     * Get FileConfiguration for this file.
     *
     * @return FileConfiguration of this file.
     */
    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }
}
