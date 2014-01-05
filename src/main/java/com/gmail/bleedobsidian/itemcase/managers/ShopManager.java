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

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.orders.Order;
import com.gmail.bleedobsidian.itemcase.util.tellraw.JSONChatClickEventType;
import com.gmail.bleedobsidian.itemcase.util.tellraw.JSONChatColor;
import com.gmail.bleedobsidian.itemcase.util.tellraw.JSONChatExtra;
import com.gmail.bleedobsidian.itemcase.util.tellraw.JSONChatFormat;
import com.gmail.bleedobsidian.itemcase.util.tellraw.JSONChatHoverEventType;
import com.gmail.bleedobsidian.itemcase.util.tellraw.JSONChatMessage;

public class ShopManager {
    private HashMap<Player, Order> orders = new HashMap<Player, Order>();

    public void addPendingOrder(Itemcase itemcase, Player player) {
        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Stop-Chat"));

        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Shop-Message1"));

        if (itemcase.getItemStack().hasItemMeta()
                && itemcase.getItemStack().getItemMeta().getDisplayName() != null) {
            PlayerLogger.message(
                    player,
                    Language.getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Message2",
                            new String[] {
                                    "%Item%",
                                    itemcase.getItemStack().getItemMeta()
                                            .getDisplayName() }));
        } else {
            PlayerLogger
                    .message(
                            player,
                            Language.getLanguageFile().getMessage(
                                    "Player.ItemCase.Shop-Message2",
                                    new String[] {
                                            "%Item%",
                                            itemcase.getItemStack().getType()
                                                    .name() }));
        }

        if (itemcase.canBuy()) {
            if (itemcase.getBuyPrice() >= 1) {
                PlayerLogger
                        .message(
                                player,
                                Language.getLanguageFile()
                                        .getMessage(
                                                "Player.ItemCase.Shop-Message3",
                                                new String[] {
                                                        "%Cost%",
                                                        ""
                                                                + itemcase
                                                                        .getBuyPrice(),
                                                        "%Currency%",
                                                        Vault.getEconomy()
                                                                .currencyNamePlural() }));
            } else {
                PlayerLogger.message(
                        player,
                        Language.getLanguageFile().getMessage(
                                "Player.ItemCase.Shop-Message3",
                                new String[] {
                                        "%Cost%",
                                        "" + itemcase.getBuyPrice(),
                                        "%Currency%",
                                        Vault.getEconomy()
                                                .currencyNameSingular() }));
            }
        }

        if (itemcase.canSell()) {
            if (itemcase.getSellPrice() >= 1) {
                PlayerLogger
                        .message(
                                player,
                                Language.getLanguageFile()
                                        .getMessage(
                                                "Player.ItemCase.Shop-Message4",
                                                new String[] {
                                                        "%Cost%",
                                                        ""
                                                                + itemcase
                                                                        .getSellPrice(),
                                                        "%Currency%",
                                                        Vault.getEconomy()
                                                                .currencyNamePlural() }));
            } else {
                PlayerLogger.message(
                        player,
                        Language.getLanguageFile().getMessage(
                                "Player.ItemCase.Shop-Message4",
                                new String[] {
                                        "%Cost%",
                                        "" + itemcase.getSellPrice(),
                                        "%Currency%",
                                        Vault.getEconomy()
                                                .currencyNameSingular() }));
            }
        }

        JSONChatMessage message = new JSONChatMessage();
        message.addText("[ItemCase]: ", JSONChatColor.BLUE, null);
        message.addText(
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Shop-Message5")
                        + " ", JSONChatColor.GREEN, null);

        JSONChatExtra extra = new JSONChatExtra(Language.getLanguageFile()
                .getMessage("Player.ItemCase.Shop-Amount-Button"),
                JSONChatColor.GOLD, Arrays.asList(JSONChatFormat.BOLD));
        extra.setHoverEvent(
                JSONChatHoverEventType.SHOW_TEXT,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Shop-Amount-Button-Hover"));
        extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND,
                "/ic order amount");
        message.addExtra(extra);

        message.sendToPlayer(player);

        if (itemcase.canBuy()) {
            JSONChatMessage messageBuy = new JSONChatMessage();
            messageBuy.addText("[ItemCase]: ", JSONChatColor.BLUE, null);
            messageBuy.addText(
                    Language.getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Message6")
                            + " ", JSONChatColor.GREEN, null);

            JSONChatExtra extraBuy = new JSONChatExtra(Language
                    .getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Buy-Button"),
                    JSONChatColor.GOLD, Arrays.asList(JSONChatFormat.BOLD));
            extraBuy.setHoverEvent(
                    JSONChatHoverEventType.SHOW_TEXT,
                    Language.getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Buy-Button-Hover"));
            extraBuy.setClickEvent(JSONChatClickEventType.RUN_COMMAND,
                    "/ic order buy");
            messageBuy.addExtra(extraBuy);

            messageBuy.sendToPlayer(player);
        }

        if (itemcase.canSell()) {
            JSONChatMessage messageSell = new JSONChatMessage();
            messageSell.addText("[ItemCase]: ", JSONChatColor.BLUE, null);
            messageSell.addText(
                    Language.getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Message7")
                            + " ", JSONChatColor.GREEN, null);

            JSONChatExtra extraSell = new JSONChatExtra(Language
                    .getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Sell-Button"),
                    JSONChatColor.GOLD, Arrays.asList(JSONChatFormat.BOLD));
            extraSell.setHoverEvent(
                    JSONChatHoverEventType.SHOW_TEXT,
                    Language.getLanguageFile().getMessage(
                            "Player.ItemCase.Shop-Sell-Button-Hover"));
            extraSell.setClickEvent(JSONChatClickEventType.RUN_COMMAND,
                    "/ic order sell");
            messageSell.addExtra(extraSell);

            messageSell.sendToPlayer(player);
        }

        JSONChatMessage messageCancel = new JSONChatMessage();
        messageCancel.addText("[ItemCase]: ", JSONChatColor.BLUE, null);

        JSONChatExtra extraCancel = new JSONChatExtra(Language
                .getLanguageFile().getMessage(
                        "Player.ItemCase.Cancel-Order-Button"),
                JSONChatColor.GOLD, Arrays.asList(JSONChatFormat.BOLD));
        extraCancel.setHoverEvent(
                JSONChatHoverEventType.SHOW_TEXT,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Cancel-Order-Button-Hover"));
        extraCancel.setClickEvent(JSONChatClickEventType.RUN_COMMAND,
                "/ic order cancel");

        messageCancel.addExtra(extraCancel);
        messageCancel.sendToPlayer(player);

        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Shop-Message8"));

        this.orders.put(player, new Order(itemcase));
    }

    public void removePendingOrder(Player player) {
        this.orders.remove(player);
        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Start-Chat"));
    }

    public boolean isPendingOrder(Player player) {
        return this.orders.containsKey(player) ? true : false;
    }

    public Order getOrder(Player player) {
        if (this.isPendingOrder(player)) {
            return this.orders.get(player);
        } else {
            return null;
        }
    }

    public HashMap<Player, Order> getOrders() {
        return this.orders;
    }
}
