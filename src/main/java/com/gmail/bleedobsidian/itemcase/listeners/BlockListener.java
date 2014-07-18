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
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Block related event listener. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            List<Itemcase> itemcases = new ArrayList<Itemcase>(ItemCase.
                    getInstance()
                    .getItemcaseManager().getItemcases());

            for (Itemcase itemcase : itemcases) {
                if (event.getBlock().equals(itemcase.getBlock())) {
                    if (itemcase.getOwnerName().equalsIgnoreCase(
                            event.getPlayer().getName())
                            || event.getPlayer().hasPermission(
                                    "itemcase.destroy.other")) {
                        ItemCase.getInstance().getItemcaseManager().
                                destroyItemcase(
                                        itemcase, event.getPlayer());

                        PlayerLogger.message(
                                event.getPlayer(),
                                Language.getLanguageFile().getMessage(
                                        "Player.ItemCase.Destroyed"));
                    } else {
                        event.setCancelled(true);

                        PlayerLogger
                                .message(
                                        event.getPlayer(),
                                        Language.getLanguageFile()
                                        .getMessage(
                                                "Player.ItemCase.Destroyed-Permission"));

                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlockAgainst();

            if (ItemCase.getInstance().getItemcaseManager().isItemcaseAt(
                    block.getLocation())) {
                if (!event.getPlayer().isSneaking()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
