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

import java.util.ListIterator;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;

public class OrderCommand {
    public static void order(ItemCase plugin, Player player, String[] args) {
        LanguageFile language = Language.getLanguageFile();

        if (!(args.length == 2)) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Order.Usage"));
            return;
        }

        if (!plugin.getShopManager().isPendingOrder(player)) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Order.No-Order"));
            return;
        }

        if (args[1].equalsIgnoreCase("amount")) {
            if (!plugin.getAmountManager().isPendingAmount(player)) {
                plugin.getAmountManager().addPendingAmount(player);
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Amount-Message"));
                return;
            }
        } else if (args[1].equalsIgnoreCase("cancel")) {
            PlayerLogger.message(player,
                    language.getMessage("Player.Order.Canceled"));
            plugin.getShopManager().removePendingOrder(player);
            return;
        } else if (args[1].equalsIgnoreCase("buy")) {
            if (!player.hasPermission("itemcase.buy")) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Permission-Itemcase"));
                return;
            }

            if (!Vault.getEconomy().hasAccount(player.getName(),
                    player.getWorld().getName())) {
                Vault.getEconomy().createPlayerAccount(player.getName(),
                        player.getWorld().getName());
            }

            double balance = Vault.getEconomy().getBalance(player.getName(),
                    player.getWorld().getName());
            double price = plugin.getShopManager().getOrder(player)
                    .getItemcase().getBuyPrice()
                    * plugin.getShopManager().getOrder(player).getAmount();

            if (!(balance >= price)) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Balance-Error"));
                return;
            }

            EconomyResponse response = Vault.getEconomy().withdrawPlayer(
                    player.getName(), player.getWorld().getName(), price);

            if (response.transactionSuccess()) {
                ItemStack items = plugin.getShopManager().getOrder(player)
                        .getItemcase().getItemStack().clone();
                items.setAmount(plugin.getShopManager().getOrder(player)
                        .getAmount());

                player.getInventory().addItem(items);

                if (price > 1) {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Withdraw", new String[] { "%Amount%",
                                    "" + price, "%Currency%",
                                    Vault.getEconomy().currencyNamePlural() }));
                } else {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Order.Withdraw",
                                    new String[] {
                                            "%Amount%",
                                            "" + price,
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNameSingular() }));
                }

                PlayerLogger.message(player, language.getMessage(
                        "Player.Order.Bought-Items",
                        new String[] {
                                "%Amount%",
                                ""
                                        + plugin.getShopManager()
                                                .getOrder(player).getAmount(),
                                "%Item%", items.getType().name() }));

                PlayerLogger.message(player, Language.getLanguageFile()
                        .getMessage("Player.Order.Amount-End"));

                plugin.getShopManager().removePendingOrder(player);
            } else {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Transaction-Failed"));
                plugin.getShopManager().removePendingOrder(player);
                return;
            }
        } else if (args[1].equalsIgnoreCase("sell")) {
            if (!player.hasPermission("itemcase.sell")) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Permission-Itemcase"));
                return;
            }

            if (!Vault.getEconomy().hasAccount(player.getName(),
                    player.getWorld().getName())) {
                Vault.getEconomy().createPlayerAccount(player.getName(),
                        player.getWorld().getName());
            }

            double itemAmount = OrderCommand.getAmountOf(player, plugin
                    .getShopManager().getOrder(player).getItemcase()
                    .getItemStack().clone());
            double price = plugin.getShopManager().getOrder(player)
                    .getItemcase().getBuyPrice()
                    * plugin.getShopManager().getOrder(player).getAmount();

            if (itemAmount < plugin.getShopManager().getOrder(player)
                    .getAmount()) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Item-Error"));
                return;
            }

            EconomyResponse response = Vault.getEconomy().depositPlayer(
                    player.getName(), player.getWorld().getName(), price);

            if (response.transactionSuccess()) {
                ItemStack items = plugin.getShopManager().getOrder(player)
                        .getItemcase().getItemStack().clone();
                items.setAmount(plugin.getShopManager().getOrder(player)
                        .getAmount());

                player.getInventory().removeItem(items);

                if (price > 1) {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Deposit", new String[] { "%Amount%",
                                    "" + price, "%Currency%",
                                    Vault.getEconomy().currencyNamePlural() }));
                } else {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Order.Deposit",
                                    new String[] {
                                            "%Amount%",
                                            "" + price,
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNameSingular() }));
                }

                PlayerLogger.message(player, language.getMessage(
                        "Player.Order.Sold-Items",
                        new String[] {
                                "%Amount%",
                                ""
                                        + plugin.getShopManager()
                                                .getOrder(player).getAmount(),
                                "%Item%", items.getType().name() }));

                PlayerLogger.message(player, Language.getLanguageFile()
                        .getMessage("Player.Order.Amount-End"));

                plugin.getShopManager().removePendingOrder(player);
            } else {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Transaction-Failed"));
                plugin.getShopManager().removePendingOrder(player);
                return;
            }
        }
    }

    private static int getAmountOf(Player player, ItemStack itemStack) {
        int amount = 0;
        ListIterator<ItemStack> it = player.getInventory().iterator();

        while (it.hasNext()) {
            ItemStack current = it.next();

            if (current != null) {
                if (current.isSimilar(itemStack)) {
                    amount += current.getAmount();
                }
            }
        }

        return amount;
    }
}
