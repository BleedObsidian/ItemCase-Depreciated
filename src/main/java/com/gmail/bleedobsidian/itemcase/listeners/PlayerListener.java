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

package com.gmail.bleedobsidian.itemcase.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

public class PlayerListener implements Listener {
    private ItemcaseManager itemcaseManager;

    public PlayerListener(ItemcaseManager itemcaseManager) {
        this.itemcaseManager = itemcaseManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && player.isSneaking()) {
            Block block = event.getClickedBlock();

            if (block.getType() == Material.STEP
                    || block.getType() == Material.WOOD_STEP) {
                if (!this.itemcaseManager.isItemcaseCreatedAt(block
                        .getLocation())) {
                    ItemStack itemStack = player.getItemInHand();

                    if (itemStack.getType() != Material.AIR) {
                        if (player.hasPermission("itemcase.create")) {
                            Location location = block.getLocation();

                            ItemStack itemStackCopy = itemStack.clone();
                            itemStackCopy.setAmount(1);

                            this.itemcaseManager.createItemcase(itemStackCopy,
                                    location, player);

                            PlayerLogger.message(
                                    player,
                                    Language.getLanguageFile().getMessage(
                                            "Player.ItemCase.Created"));
                        } else {
                            event.setCancelled(true);

                            PlayerLogger
                                    .message(
                                            player,
                                            Language.getLanguageFile()
                                                    .getMessage(
                                                            "Player.ItemCase.Created-Permission"));

                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        for (Itemcase itemcase : this.itemcaseManager.getItemcases()) {
            if (event.getItem().equals(itemcase.getItem())) {
                event.setCancelled(true);
            }
        }
    }
}
