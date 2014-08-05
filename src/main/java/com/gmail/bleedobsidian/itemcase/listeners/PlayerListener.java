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

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.WorldGuard;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;
import com.gmail.bleedobsidian.itemcase.tasks.PickupPointSpawner;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Player related event listener. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (ItemCase.getInstance().getConfigFile().getFileConfiguration()
                .getBoolean("Options.Disable-Sneak-Create")) {
            return;
        }

        ItemCase itemcase = ItemCase.getInstance();
        Player player = event.getPlayer();

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event
                .getAction() == Action.RIGHT_CLICK_AIR)
                && player.isSneaking()) {

            Block block = event.getClickedBlock();

            if (block == null) {
                if (player.getTargetBlock(null, 100) != null
                        && player.getTargetBlock(null, 100).getType() != Material.AIR) {
                    block = player.getTargetBlock(null, 100);
                } else {
                    return;
                }
            }

            for (int id : itemcase.getConfigFile()
                    .getFileConfiguration().getIntegerList("Blocks")) {

                if (block.getType().getId() != id) {
                    return;
                }

                if (itemcase.getItemcaseManager().
                        isItemcaseAt(
                                block.getLocation())) {
                    return;
                }

                ItemStack itemStack = player.getItemInHand();

                if (itemStack.getType() == Material.AIR) {
                    return;
                }

                if (!player
                        .hasPermission(
                                "itemcase.create.showcase")) {
                    return;
                }

                if (WorldGuard.isEnabled()
                        && !WorldGuard
                        .getWorldGuardPlugin()
                        .canBuild(player, block)) {
                    PlayerLogger
                            .messageLanguage(
                                    player,
                                    "Player.ItemCase.Created-Region");
                    event.setCancelled(true);
                    return;
                }

                Location location = block.getLocation();

                ItemStack itemStackCopy = itemStack.clone();
                itemStackCopy.setAmount(1);

                ItemCase.getInstance().getItemcaseManager()
                        .createItemcase(itemStackCopy,
                                location, player);

                PlayerLogger
                        .messageLanguage(
                                player,
                                "Player.ItemCase.Created");
                event.setCancelled(true);
                return;
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!ItemCase.getInstance().getItemcaseManager().isItemcaseAt(
                    event.getClickedBlock().getLocation())) {
                return;
            }

            if (ItemCase.getInstance().getSelectionManager().
                    isPendingSelection(
                            player)) {
                itemcase.getSelectionManager().onPlayerSelect(
                        player,
                        itemcase.getItemcaseManager().
                        getItemcaseAt(
                                event.getClickedBlock().getLocation()));
                event.setCancelled(true);
                return;
            } else {
                if ((itemcase
                        .getItemcaseManager()
                        .getItemcaseAt(
                                event.getClickedBlock().getLocation())
                        .getOwnerName()
                        .equals(event.getPlayer().getName()) || player
                        .hasPermission("itemcase.destroy.other")) && event.
                        getAction() == Action.LEFT_CLICK_BLOCK) {
                    return;
                }

                if (itemcase
                        .getItemcaseManager()
                        .getItemcaseAt(
                                event.getClickedBlock()
                                .getLocation()).getType() != ItemcaseType.SHOP) {
                    return;
                }

                if (!itemcase.getShopManager()
                        .isPendingOrder(player)) {
                    ItemCase.getInstance()
                            .getShopManager()
                            .addPendingOrder(
                                    ItemCase.
                                    getInstance()
                                    .getItemcaseManager().
                                    getItemcaseAt(
                                            event.
                                            getClickedBlock().
                                            getLocation()),
                                    player);
                    return;
                } else {
                    PlayerLogger
                            .messageLanguage(
                                    player,
                                    "Player.ItemCase.Shop-Order-Processing-1");

                    PlayerLogger
                            .messageLanguage(
                                    player,
                                    "Player.ItemCase.Shop-Order-Processing-2");
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("ItemCase")) {
            event.setCancelled(true);
        } else { // Fail safe
            for (Itemcase itemcase : ItemCase.getInstance().getItemcaseManager()
                    .getItemcases()) {
                if (event.getItem().equals(itemcase.getItem())) {
                    if (itemcase.getType() == ItemcaseType.PICKUP_POINT) {
                        Bukkit.getScheduler().runTaskLater(ItemCase.
                                getInstance(), new PickupPointSpawner(itemcase),
                                itemcase.getPickupPointInterval());
                        itemcase.setWaitingForSpawn(true);
                        return;
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != null) {
            if (ItemCase.getInstance().getInputManager().isPendingInput(
                    event.getPlayer())) {
                event.setCancelled(true);
                ItemCase.getInstance().getInputManager().setPendingInput(
                        event.getPlayer(), event.getMessage());
            } else if (ItemCase.getInstance().getShopManager().isPendingOrder(
                    event.getPlayer())) {
                event.setCancelled(true);
            }

            Set<Player> playersOrdering = ItemCase.getInstance().
                    getShopManager().getOrders()
                    .keySet();

            for (Player player : playersOrdering) {
                event.getRecipients().remove(player);
            }
        }
    }
}
