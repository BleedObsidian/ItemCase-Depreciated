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

import java.io.IOException;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class allows you to create a world specific ConfigFile.
 *
 * @author Jesse Prescott
 */
public class WorldFile {

    /**
     * Bukkit World.
     */
    private World world;

    /**
     * ConfigFile.
     */
    private ConfigFile configFile;

    /**
     * New world config.
     *
     * @param world World.
     */
    public WorldFile(World world) {
        this.world = world;

        this.configFile = new ConfigFile(world.getName() + "/itemcases.yml",
                "itemcases.yml");
    }

    /**
     * Load/Create world config file.
     *
     * @param plugin JavaPlugin.
     * @throws java.io.IOException Fails to load config file.
     */
    public void load(JavaPlugin plugin) throws IOException {
        this.configFile.load(plugin);
    }

    /**
     * Get ConfigFile of world.
     *
     * @return ConfigFile.
     */
    public ConfigFile getConfigFile() {
        return this.configFile;
    }

    /**
     * Get world.
     *
     * @return World.
     */
    public World getWorld() {
        return this.world;
    }
}
