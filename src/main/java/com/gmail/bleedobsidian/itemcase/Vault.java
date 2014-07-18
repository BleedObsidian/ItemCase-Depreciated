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

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class allows you to load and access vault's economy plugins. (Only used
 * internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class Vault {

    /**
     * Economy API.
     */
    private static Economy economy;

    /**
     * Load vault and an economy plugin.
     *
     * @param plugin JavaPlugin.
     * @return If successfully loaded.
     */
    public static boolean load(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer()
                .getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (economyProvider != null) {
            Vault.economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    /**
     * Get economy API.
     *
     * @return Economy plugin.
     */
    public static Economy getEconomy() {
        return Vault.economy;
    }
}
