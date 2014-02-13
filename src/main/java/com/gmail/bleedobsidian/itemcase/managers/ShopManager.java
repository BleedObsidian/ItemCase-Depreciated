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

import java.text.DecimalFormat;
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

/**
 * A manager to handle all pending orders and transactions. (Only used
 * internally, please use API)
 * 
 * @author BleedObsidian (Jesse Prescott)
 */
public class ShopManager {
    private HashMap<Player, Order> orders = new HashMap<Player, Order>();

    private final DecimalFormat format = new DecimalFormat("0.00");

    /**
     * Add new pending order.
     * 
     * @param itemcase
     *            - Itemcase.
     * @param player
     *            - Player.
     */
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
                                                                + this.format
                                                                        .format(itemcase
                                                                                .getBuyPrice()),
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
                                        ""
                                                + this.format.format(itemcase
                                                        .getBuyPrice()),
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
                                                                + this.format
                                                                        .format(itemcase
                                                                                .getSellPrice()),
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
                                        ""
                                                + this.format.format(itemcase
                                                        .getSellPrice()),
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
                "/itemc order amount");
        message.addExtra(extra);
        message.addText(
                " "
                        + Language.getLanguageFile().getMessage(
                                "Player.ItemCase.Button-Idicator"),
                JSONChatColor.GREEN, null);
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
                    "/itemc order buy");
            messageBuy.addExtra(extraBuy);
            messageBuy.addText(
                    "                 "
                            + Language.getLanguageFile().getMessage(
                                    "Player.ItemCase.Button-Idicator"),
                    JSONChatColor.GREEN, null);
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
                    "/itemc order sell");
            messageSell.addExtra(extraSell);
            messageSell.addText(
                    "               "
                            + Language.getLanguageFile().getMessage(
                                    "Player.ItemCase.Button-Idicator"),
                    JSONChatColor.GREEN, null);
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
                "/itemc order cancel");

        messageCancel.addExtra(extraCancel);
        message.addText(
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Button-Idicator"),
                JSONChatColor.GREEN, null);
        messageCancel.sendToPlayer(player);

        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Shop-Message8"));

        this.orders.put(player, new Order(itemcase, player));
    }

    /**
     * Remove pending order.
     * 
     * @param player
     *            - Player.
     */
    public void removePendingOrder(Player player) {
        this.getOrder(player).cancel();
        this.orders.remove(player);
        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Start-Chat"));
    }

    /**
     * If player has a current pending order.
     * 
     * @param player
     *            - Player.
     * @return - If player has current pending order.
     */
    public boolean isPendingOrder(Player player) {
        return this.orders.containsKey(player) ? true : false;
    }

    /**
     * Get current pending order.
     * 
     * @param player
     *            - Player.
     * @return - Current pending order (Null if no order pending)
     */
    public Order getOrder(Player player) {
        if (this.isPendingOrder(player)) {
            return this.orders.get(player);
        } else {
            return null;
        }
    }

    /**
     * Get all current orders.
     * 
     * @return - List of all orders.
     */
    public HashMap<Player, Order> getOrders() {
        return this.orders;
    }
}
