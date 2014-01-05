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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.command.listeners.ModifySelectionListener;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;

public class ModifyCommand {
    public static void modify(ItemCase plugin, Player player, String[] args) {
        LanguageFile language = Language.getLanguageFile();

        if (!(args.length >= 2)) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Usage"));
            return;
        }

        if (args[1].equalsIgnoreCase("shop")) {
            if (args.length != 4) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Shop.Usage"));
                return;
            }

            if (!(args[2].equalsIgnoreCase("buy") || args[2]
                    .equalsIgnoreCase("sell"))) {
                PlayerLogger.message(player, language.getMessage(
                        "Player.Modify.Shop.Invalid-Type", new String[] {
                                "%type%", args[2] }));
                return;
            }

            if (args[2].equalsIgnoreCase("buy")) {
                if (!player.hasPermission("itemcase.create.shop.buy")) {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Permission-Itemcase"));
                    return;
                }

                PlayerLogger.message(player,
                        language.getMessage("Player.Permission-Itemcase"));

                try {
                    Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    PlayerLogger.message(player, language
                            .getMessage("Player.Modify.Shop.Invalid-Price"));
                    return;
                }

                ModifySelectionListener listener = new ModifySelectionListener(
                        plugin, ItemcaseType.SHOP, args);
                plugin.getSelectionManager().addPendingSelection(listener,
                        player);

                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Select"));
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Cancel"));

                return;
            } else if (args[2].equalsIgnoreCase("sell")) {
                if (!player.hasPermission("itemcase.create.shop.sell")) {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Permission-Itemcase"));
                    return;
                }

                PlayerLogger.message(player,
                        language.getMessage("Player.Permission-Itemcase"));

                try {
                    Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    PlayerLogger.message(player, language
                            .getMessage("Player.Modify.Shop.Invalid-Price"));
                    return;
                }

                ModifySelectionListener listener = new ModifySelectionListener(
                        plugin, ItemcaseType.SHOP, args);
                plugin.getSelectionManager().addPendingSelection(listener,
                        player);

                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Select"));
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Cancel"));
                return;
            }
        } else if (args[1].equalsIgnoreCase("showcase")) {
            if (!player.hasPermission("itemcase.create.showcase")) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Permission-Itemcase"));
                return;
            }

            if (args.length != 2) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Showcase.Usage"));
                return;
            }

            ModifySelectionListener listener = new ModifySelectionListener(
                    plugin, ItemcaseType.SHOWCASE, args);
            plugin.getSelectionManager().addPendingSelection(listener, player);

            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Select"));
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Cancel"));
            return;
        } else if (args[1].equalsIgnoreCase("infinite")) {
            if (!player.hasPermission("itemcase.create.infinite")) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Permission-Itemcase"));
                return;
            }

            if (args.length != 3) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Infinite.Usage"));
                return;
            }

            if (!(args[2].equalsIgnoreCase("true") || args[2]
                    .equalsIgnoreCase("false"))) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Infinite.Usage"));
                return;
            }

            ModifySelectionListener listener = new ModifySelectionListener(
                    plugin, args);
            plugin.getSelectionManager().addPendingSelection(listener, player);

            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Select"));
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Cancel"));

            return;
        } else {
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Usage"));
            return;
        }
    }

    public static void selected(ItemCase plugin, Player player, String[] args,
            Itemcase itemcase, ItemcaseType type) {
        LanguageFile language = Language.getLanguageFile();

        itemcase.setType(type);

        if (type == ItemcaseType.SHOWCASE) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Showcase.Successful"));
        } else if (type == ItemcaseType.SHOP) {
            if (args[2].equalsIgnoreCase("buy")) {
                itemcase.setCanBuy(true);
                itemcase.setBuyPrice(Double.parseDouble(args[3]));

                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Shop.Buy"));
            } else if (args[2].equalsIgnoreCase("sell")) {
                itemcase.setCanSell(true);
                itemcase.setSellPrice(Double.parseDouble(args[3]));

                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Shop.Sell"));
            }
        }

        plugin.getItemcaseManager().saveItemcase(itemcase);
    }

    public static void selectedInfinite(ItemCase plugin, Player player,
            String[] args, Itemcase itemcase) {
        LanguageFile language = Language.getLanguageFile();

        boolean value = Boolean.parseBoolean(args[2]);
        itemcase.setInfinite(value);

        itemcase.setInventory(Bukkit.createInventory(null, 54,
                "ItemCase Storage"));
        plugin.getItemcaseManager().saveItemcase(itemcase);

        PlayerLogger.message(player, language.getMessage(
                "Player.Modify.Infinite.Successful", new String[] {
                        "%Boolean%", "" + value }));
    }
}
