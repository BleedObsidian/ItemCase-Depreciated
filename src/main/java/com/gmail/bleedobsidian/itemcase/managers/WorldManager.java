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

import com.gmail.bleedobsidian.itemcase.configurations.WorldFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A manager to handle all world configurations. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class WorldManager {

    /**
     * Worlds.
     */
    private List<World> worlds = new ArrayList<World>();

    /**
     * World save files.
     */
    private HashMap<String, WorldFile> worldSaveFiles = new HashMap<String, WorldFile>();

    /**
     * New WorldManager.
     *
     * @param plugin JavaPlugin.
     */
    public WorldManager(JavaPlugin plugin) {
        this.worlds = plugin.getServer().getWorlds();

        for (World world : worlds) {
            WorldFile saveFile = new WorldFile(world);

            this.worldSaveFiles.put(world.getName(), saveFile);
        }
    }

    /**
     * Load world files.
     *
     * @param plugin JavaPlugin.
     * @throws IOException Failes to load files.
     */
    public void load(JavaPlugin plugin) throws IOException {
        for (Map.Entry<String, WorldFile> entry : this.worldSaveFiles
                .entrySet()) {
            entry.getValue().load(plugin);
        }
    }

    /**
     * @return - All worlds.
     */
    public List<World> getWorlds() {
        return this.worlds;
    }

    /**
     * Get config for given world.
     *
     * @param world World.
     * @return WorldFile.
     */
    public WorldFile getWorldFile(World world) {
        return this.worldSaveFiles.get(world.getName());
    }

    /**
     * Get config for given world.
     *
     * @param worldName World name.
     * @return WorldFile.
     */
    public WorldFile getWorldFile(String worldName) {
        return this.worldSaveFiles.get(worldName);
    }
}
