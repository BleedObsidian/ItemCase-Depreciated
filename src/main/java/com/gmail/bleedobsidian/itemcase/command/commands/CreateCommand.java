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
package com.gmail.bleedobsidian.itemcase.command.commands;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.WorldGuard;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Create command. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class CreateCommand {

    /**
     * Run command.
     *
     * @param player Player that ran command.
     * @param args Command arguments.
     */
    public static void create(Player player, String[] args) {
        LanguageFile language = Language.getLanguageFile();

        if (!player.hasPermission("itemcase.create.showcase")) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Permission"));
            return;
        }

        Block block = player.getTargetBlock(null, 5);

        if (block == null || block.getType() == Material.AIR) {
            PlayerLogger.message(player,
                    language.getMessage("Player.ItemCase.Created-Block"));
            return;
        }

        if (player.getItemInHand().getType() == Material.AIR) {
            PlayerLogger.message(player,
                    language.getMessage("Player.ItemCase.Created-Hand"));
            return;
        }

        if (!ItemCase.getInstance().getConfigFile().getFileConfiguration()
                .getIntegerList("Blocks").contains(block.getType().getId())) {
            PlayerLogger.message(player,
                    language.getMessage("Player.ItemCase.Created-Block"));
            return;
        }

        if (WorldGuard.isEnabled()
                && !WorldGuard.getWorldGuardPlugin().canBuild(player, block)) {
            PlayerLogger.message(
                    player,
                    Language.getLanguageFile().getMessage(
                            "Player.ItemCase.Created-Region"));
            return;
        }

        Location location = block.getLocation();

        ItemStack itemStackCopy = player.getItemInHand().clone();
        itemStackCopy.setAmount(1);

        ItemCase.getInstance().getItemcaseManager()
                .createItemcase(itemStackCopy, location, player);

        PlayerLogger.message(player,
                Language.getLanguageFile()
                .getMessage("Player.ItemCase.Created"));
        return;
    }
}
