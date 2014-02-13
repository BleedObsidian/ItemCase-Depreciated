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

import java.text.DecimalFormat;
import java.util.ListIterator;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

/**
 * Order Command. (Only used internally)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class OrderCommand {
    private static final DecimalFormat format = new DecimalFormat("0.00");

    /**
     * Run command.
     * 
     * @param plugin
     *            - ItemCase plugin.
     * @param player
     *            - Player.
     * @param args
     *            - Arguments.
     */
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
            PlayerLogger.message(
                    player,
                    Language.getLanguageFile().getMessage(
                            "Player.Order.Amount-End"));
            plugin.getShopManager().removePendingOrder(player);
            return;
        } else if (args[1].equalsIgnoreCase("buy")
                && plugin.getShopManager().getOrder(player).getItemcase()
                        .canBuy()) {
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

            if (!plugin.getShopManager().getOrder(player).getItemcase()
                    .isInfinite()) {
                double itemAmount = OrderCommand.getAmountOf(plugin
                        .getShopManager().getOrder(player).getItemcase()
                        .getInventory(),
                        plugin.getShopManager().getOrder(player).getItemcase()
                                .getItemStack().clone());

                if (!(itemAmount >= plugin.getShopManager().getOrder(player)
                        .getAmount())) {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Order.No-Stock"));
                    PlayerLogger.message(player, Language.getLanguageFile()
                            .getMessage("Player.Order.Amount-End"));
                    return;
                }
            }

            if (!(balance >= price)) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Balance-Error"));
                return;
            }

            EconomyResponse response = Vault.getEconomy().withdrawPlayer(
                    player.getName(), player.getWorld().getName(), price);

            if (!plugin.getShopManager().getOrder(player).getItemcase()
                    .isInfinite()) {
                EconomyResponse responseOwner = Vault.getEconomy()
                        .depositPlayer(
                                plugin.getShopManager().getOrder(player)
                                        .getItemcase().getOwnerName(),
                                player.getWorld().getName(), price);

                if (!responseOwner.transactionSuccess()) {
                    PlayerLogger.message(player, language
                            .getMessage("Player.Order.Transaction-Failed"));
                    plugin.getShopManager().removePendingOrder(player);
                    return;
                } else {
                    if (Bukkit.getOfflinePlayer(
                            plugin.getShopManager().getOrder(player)
                                    .getItemcase().getOwnerName()).isOnline()) {
                        Player owner = (Player) Bukkit.getPlayer(plugin
                                .getShopManager().getOrder(player)
                                .getItemcase().getOwnerName());

                        if (plugin.getShopManager().getOrder(player)
                                .getItemcase().getItemStack().hasItemMeta()
                                && plugin.getShopManager().getOrder(player)
                                        .getItemcase().getItemStack()
                                        .getItemMeta().getDisplayName() != null) {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Owner-Buy",
                                    new String[] {
                                            "%Player%",
                                            player.getName(),
                                            "%Amount%",
                                            ""
                                                    + plugin.getShopManager()
                                                            .getOrder(player)
                                                            .getAmount(),
                                            "%Item%",
                                            plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getItemcase()
                                                    .getItemStack()
                                                    .getItemMeta()
                                                    .getDisplayName() }));
                        } else {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Owner-Buy",
                                    new String[] {
                                            "%Player%",
                                            player.getName(),
                                            "%Amount%",
                                            ""
                                                    + plugin.getShopManager()
                                                            .getOrder(player)
                                                            .getAmount(),
                                            "%Item%",
                                            plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getItemcase()
                                                    .getItemStack().getType()
                                                    .name() }));
                        }

                        if (price > 1) {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Deposit",
                                    new String[] {
                                            "%Amount%",
                                            ""
                                                    + OrderCommand.format
                                                            .format(price),
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNamePlural() }));
                        } else {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Deposit",
                                    new String[] {
                                            "%Amount%",
                                            ""
                                                    + OrderCommand.format
                                                            .format(price),
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNameSingular() }));
                        }
                    }
                }
            }

            if (response.transactionSuccess()) {
                ItemStack items = plugin.getShopManager().getOrder(player)
                        .getItemcase().getItemStack().clone();
                items.setAmount(plugin.getShopManager().getOrder(player)
                        .getAmount());

                player.getInventory().addItem(items);

                if (!plugin.getShopManager().getOrder(player).getItemcase()
                        .isInfinite()) {
                    plugin.getShopManager().getOrder(player).getItemcase()
                            .getInventory().removeItem(items);
                }

                if (price > 1) {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Withdraw", new String[] { "%Amount%",
                                    "" + OrderCommand.format.format(price),
                                    "%Currency%",
                                    Vault.getEconomy().currencyNamePlural() }));
                } else {
                    PlayerLogger.message(player,
                            language.getMessage(
                                    "Player.Order.Withdraw",
                                    new String[] {
                                            "%Amount%",
                                            ""
                                                    + OrderCommand.format
                                                            .format(price),
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNameSingular() }));
                }

                if (items.hasItemMeta()
                        && items.getItemMeta().getDisplayName() != null) {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Bought-Items",
                            new String[] {
                                    "%Amount%",
                                    ""
                                            + plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getAmount(), "%Item%",
                                    items.getItemMeta().getDisplayName() }));
                } else {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Bought-Items",
                            new String[] {
                                    "%Amount%",
                                    ""
                                            + plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getAmount(), "%Item%",
                                    items.getType().name() }));
                }

                PlayerLogger.message(player, Language.getLanguageFile()
                        .getMessage("Player.Order.Amount-End"));

                plugin.getShopManager().removePendingOrder(player);
            } else {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Transaction-Failed"));
                plugin.getShopManager().removePendingOrder(player);
                return;
            }
        } else if (args[1].equalsIgnoreCase("sell")
                && plugin.getShopManager().getOrder(player).getItemcase()
                        .canSell()) {
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

            double itemAmount = OrderCommand.getAmountOf(player.getInventory(),
                    plugin.getShopManager().getOrder(player).getItemcase()
                            .getItemStack().clone());
            double price = plugin.getShopManager().getOrder(player)
                    .getItemcase().getSellPrice()
                    * plugin.getShopManager().getOrder(player).getAmount();

            if (itemAmount < plugin.getShopManager().getOrder(player)
                    .getAmount()) {
                PlayerLogger.message(player,
                        language.getMessage("Player.Order.Item-Error"));
                return;
            }

            Itemcase itemcase = plugin.getShopManager().getOrder(player)
                    .getItemcase();
            if (!itemcase.isInfinite()) {
                if (!(Vault.getEconomy().getBalance(itemcase.getOwnerName()) >= price)) {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Order.Owner-Balance"));
                    return;
                }

                EconomyResponse responseOwner = Vault.getEconomy()
                        .withdrawPlayer(itemcase.getOwnerName(),
                                player.getWorld().getName(), price);

                if (!responseOwner.transactionSuccess()) {
                    PlayerLogger.message(player, language
                            .getMessage("Player.Order.Transaction-Failed"));
                    plugin.getShopManager().removePendingOrder(player);
                    return;
                } else {
                    if (Bukkit.getOfflinePlayer(
                            plugin.getShopManager().getOrder(player)
                                    .getItemcase().getOwnerName()).isOnline()) {
                        Player owner = (Player) Bukkit.getPlayer(plugin
                                .getShopManager().getOrder(player)
                                .getItemcase().getOwnerName());

                        if (plugin.getShopManager().getOrder(player)
                                .getItemcase().getItemStack().hasItemMeta()
                                && plugin.getShopManager().getOrder(player)
                                        .getItemcase().getItemStack()
                                        .getItemMeta().getDisplayName() != null) {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Owner-Sell",
                                    new String[] {
                                            "%Player%",
                                            player.getName(),
                                            "%Amount%",
                                            ""
                                                    + plugin.getShopManager()
                                                            .getOrder(player)
                                                            .getAmount(),
                                            "%Item%",
                                            plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getItemcase()
                                                    .getItemStack()
                                                    .getItemMeta()
                                                    .getDisplayName() }));
                        } else {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Owner-Sell",
                                    new String[] {
                                            "%Player%",
                                            player.getName(),
                                            "%Amount%",
                                            ""
                                                    + plugin.getShopManager()
                                                            .getOrder(player)
                                                            .getAmount(),
                                            "%Item%",
                                            plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getItemcase()
                                                    .getItemStack().getType()
                                                    .name() }));
                        }

                        if (price > 1) {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Withdraw",
                                    new String[] {
                                            "%Amount%",
                                            ""
                                                    + OrderCommand.format
                                                            .format(price),
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNamePlural() }));
                        } else {
                            PlayerLogger.message(owner, language.getMessage(
                                    "Player.Order.Withdraw",
                                    new String[] {
                                            "%Amount%",
                                            ""
                                                    + OrderCommand.format
                                                            .format(price),
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNameSingular() }));
                        }
                    }
                }
            }

            EconomyResponse response = Vault.getEconomy().depositPlayer(
                    player.getName(), player.getWorld().getName(), price);

            if (response.transactionSuccess()) {
                ItemStack items = plugin.getShopManager().getOrder(player)
                        .getItemcase().getItemStack().clone();
                items.setAmount(plugin.getShopManager().getOrder(player)
                        .getAmount());

                player.getInventory().removeItem(items);

                if (!plugin.getShopManager().getOrder(player).getItemcase()
                        .isInfinite()) {
                    plugin.getShopManager().getOrder(player).getItemcase()
                            .getInventory().addItem(items);
                }

                if (price > 1) {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Deposit", new String[] { "%Amount%",
                                    "" + OrderCommand.format.format(price),
                                    "%Currency%",
                                    Vault.getEconomy().currencyNamePlural() }));
                } else {
                    PlayerLogger.message(player,
                            language.getMessage("Player.Order.Deposit",
                                    new String[] {
                                            "%Amount%",
                                            OrderCommand.format.format(price),
                                            "%Currency%",
                                            Vault.getEconomy()
                                                    .currencyNameSingular() }));
                }

                if (items.hasItemMeta()
                        && items.getItemMeta().getDisplayName() != null) {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Sold-Items",
                            new String[] {
                                    "%Amount%",
                                    ""
                                            + plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getAmount(), "%Item%",
                                    items.getItemMeta().getDisplayName() }));
                } else {
                    PlayerLogger.message(player, language.getMessage(
                            "Player.Order.Sold-Items",
                            new String[] {
                                    "%Amount%",
                                    ""
                                            + plugin.getShopManager()
                                                    .getOrder(player)
                                                    .getAmount(), "%Item%",
                                    items.getType().name() }));
                }

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

    private static int getAmountOf(Inventory inventory, ItemStack itemStack) {
        int amount = 0;
        ListIterator<ItemStack> it = inventory.iterator();

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
