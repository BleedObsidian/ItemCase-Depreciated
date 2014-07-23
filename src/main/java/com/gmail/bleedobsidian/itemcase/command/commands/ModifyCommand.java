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
import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.command.listeners.ModifySelectionListener;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Modify Command. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ModifyCommand {

    /**
     * Run command.
     *
     * @param player Player that ran command.
     * @param args Command arguments.
     */
    public static void modify(Player player, String[] args) {
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
                        "Player.Modify.Shop.Invalid-Type", new String[]{
                            "%type%", args[2]}));
                return;
            }

            if (args[2].equalsIgnoreCase("buy")) {
                if (!Vault.isLoaded()) {
                    PlayerLogger.messageLanguage(player,
                            "Player.ItemCase.Vault-Warning");
                    return;
                }

                if (!player.hasPermission("itemcase.create.shop.buy")) {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Permission-Itemcase"));
                    return;
                }

                try {
                    Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    PlayerLogger.message(player, language
                            .getMessage("Player.Modify.Shop.Invalid-Price"));
                    return;
                }

                ModifySelectionListener listener = new ModifySelectionListener(
                        ItemcaseType.SHOP, args);
                ItemCase.getInstance().getSelectionManager().
                        addPendingSelection(listener,
                                player);

                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Select"));
                PlayerLogger.message(player,
                        language.getMessage("Player.Modify.Cancel"));

                return;
            } else if (args[2].equalsIgnoreCase("sell")) {
                if (!Vault.isLoaded()) {
                    PlayerLogger.messageLanguage(player,
                            "Player.ItemCase.Vault-Warning");
                    return;
                }

                if (!player.hasPermission("itemcase.create.shop.sell")) {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Permission-Itemcase"));
                    return;
                }

                try {
                    Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    PlayerLogger.message(player, language
                            .getMessage("Player.Modify.Shop.Invalid-Price"));
                    return;
                }

                ModifySelectionListener listener = new ModifySelectionListener(
                        ItemcaseType.SHOP, args);
                ItemCase.getInstance().getSelectionManager().
                        addPendingSelection(listener,
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
                    ItemcaseType.SHOWCASE, args);
            ItemCase.getInstance().getSelectionManager().addPendingSelection(
                    listener, player);

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

            ModifySelectionListener listener = new ModifySelectionListener(args);
            ItemCase.getInstance().getSelectionManager().addPendingSelection(
                    listener, player);

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

    /**
     * Received selection.
     *
     * @param player Player.
     * @param args Command arguments.
     * @param itemcase Selected Itemcase.
     * @param type ItemCaseType.
     */
    public static void selected(Player player, String[] args,
            Itemcase itemcase, ItemcaseType type) {
        LanguageFile language = Language.getLanguageFile();

        if (!(itemcase.getOwnerName().equals(player.getName()) || player
                .hasPermission("itemcase.modify"))) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Modify-Permission"));
            return;
        }

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

        ItemCase.getInstance().getItemcaseManager().saveItemcase(itemcase);
    }

    /**
     * Received selection.
     *
     * @param player Player.
     * @param args Command arguments.
     * @param itemcase Selected Itemcase.
     */
    public static void selectedInfinite(Player player,
            String[] args, Itemcase itemcase) {
        LanguageFile language = Language.getLanguageFile();

        if (!(itemcase.getOwnerName().equals(player.getName()) || player
                .hasPermission("itemcase.modify"))) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Modify.Modify-Permission"));
            return;
        }

        boolean value = Boolean.parseBoolean(args[2]);
        itemcase.setInfinite(value);

        itemcase.setInventory(Bukkit.createInventory(null, 54,
                "ItemCase Storage"));
        ItemCase.getInstance().getItemcaseManager().saveItemcase(itemcase);

        PlayerLogger.message(player, language.getMessage(
                "Player.Modify.Infinite.Successful", new String[]{
                    "%Boolean%", "" + value}));
    }
}
