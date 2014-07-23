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
package com.gmail.bleedobsidian.itemcase.managers.orders;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.events.ItemcasePostTransactionEvent;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.util.InventoryUtils;
import com.gmail.bleedobsidian.itemcase.util.ShopGUI;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an order.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class Order {

    /**
     * Itemcase involved.
     */
    private final Itemcase itemcase;

    /**
     * ItemStack being bought/sold.
     */
    private final ItemStack item;

    /**
     * OrderMode.
     */
    private OrderMode mode;

    /**
     * Amount.
     */
    private int amount = 1;

    /**
     * Price of order.
     */
    private double price = 0;

    /**
     * Bukkit taskID.
     */
    private final int taskID;

    /**
     * New order.
     *
     * @param itemcase Itemcase involved.
     * @param player Player involved.
     * @param item ItemStack being bought/sold.
     */
    public Order(Itemcase itemcase, final Player player, ItemStack item) {
        this.itemcase = itemcase;
        this.item = item;

        this.taskID = Bukkit.getScheduler()
                .runTaskLater(ItemCase.getInstance(), new Runnable() {
                    public void run() {
                        if (ItemCase.getInstance().getShopManager()
                        .isPendingOrder(player)) {
                            if (ItemCase.getInstance().getInputManager().
                            isPendingInput(player)) {
                                ItemCase.getInstance().getInputManager().
                                removePendingInput(player);
                            }

                            PlayerLogger.message(
                                    player,
                                    Language.getLanguageFile().getMessage(
                                            "Player.Order.Timeout"));
                            PlayerLogger.messageLanguage(player,
                                    "Player.Order.End");
                            ItemCase.getInstance().getShopManager()
                            .removePendingOrder(player);
                        }
                    }
                }, 600).getTaskId();
    }

    /**
     * Buy current order.
     *
     * @param player Player buying order.
     */
    public void buy(Player player) {
        // Create economy account
        if (!Vault.getEconomy().hasAccount(Bukkit.getOfflinePlayer(player.
                getUniqueId()), player.getWorld().getName())) {
            if (!Vault.getEconomy().createPlayerAccount(Bukkit.getOfflinePlayer(
                    player.getUniqueId()), player.getWorld().getName())) {
                ShopGUI.displayResult(player, this,
                        OrderResult.TRANSACTION_FAILED);
                return;
            }
        }

        // Check balance
        double playerBalance = Vault.getEconomy().getBalance(Bukkit.
                getOfflinePlayer(player.getUniqueId()), player.getWorld().
                getName());
        double amountDue = this.itemcase.getBuyPrice() * this.amount;

        if (playerBalance < amountDue) {
            ShopGUI.
                    displayResult(player, this, OrderResult.INSUFFICIENT_BALANCE);
            return;
        }

        // Check stock
        if (!this.itemcase.isInfinite()) {
            int stock = InventoryUtils.getAmountOf(this.itemcase.getInventory(),
                    this.item);

            if (stock < this.amount) {
                ShopGUI.displayResult(player, this,
                        OrderResult.INSUFFICIENT_STOCK);
                return;
            }
        }

        ItemcasePostTransactionEvent event = new ItemcasePostTransactionEvent(
                itemcase, player, this, OrderMode.BUY);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ShopGUI.displayResult(player, this, OrderResult.CANCELED);
            return;
        }

        // Remove funds
        EconomyResponse response = Vault.getEconomy().withdrawPlayer(Bukkit.
                getOfflinePlayer(player.getUniqueId()),
                player.getWorld().getName(), amountDue);

        if (!response.transactionSuccess()) {
            ShopGUI.displayResult(player, this, OrderResult.TRANSACTION_FAILED);
            return;
        }

        // Add owner funds
        if (!this.itemcase.isInfinite()) {
            EconomyResponse ownerResponse = Vault.getEconomy().depositPlayer(
                    Bukkit.
                    getOfflinePlayer(this.itemcase.getOwnerName()),
                    player.getWorld().getName(), amountDue);

            if (!ownerResponse.transactionSuccess()) {
                ShopGUI.displayResult(player, this,
                        OrderResult.TRANSACTION_FAILED);
                return;
            }
        }

        // Remove stock
        if (!this.itemcase.isInfinite()) {
            ItemStack itemstack = this.item.clone();
            itemstack.setAmount(this.amount);

            this.itemcase.getInventory().removeItem(itemstack);
        }

        // Add player stock
        ItemStack itemstack = this.item.clone();
        itemstack.setAmount(this.amount);

        player.getInventory().addItem(itemstack);

        ShopGUI.displayResult(player, this,
                OrderResult.BUY_SUCCESS);
        return;
    }

    /**
     * Sell current order.
     *
     * @param player Player selling order.
     */
    public void sell(Player player) {
        // Create economy account
        if (!Vault.getEconomy().hasAccount(Bukkit.getOfflinePlayer(player.
                getUniqueId()), player.getWorld().getName())) {
            if (!Vault.getEconomy().createPlayerAccount(Bukkit.getOfflinePlayer(
                    player.getUniqueId()), player.getWorld().getName())) {
                ShopGUI.displayResult(player, this,
                        OrderResult.TRANSACTION_FAILED);
                return;
            }
        }

        // Check balance
        double ownerBalance = Vault.getEconomy().getBalance(Bukkit.
                getOfflinePlayer(this.itemcase.getOwnerName()), player.
                getWorld().
                getName());
        double amountDue = this.itemcase.getSellPrice() * this.amount;

        if (ownerBalance < amountDue) {
            ShopGUI.
                    displayResult(player, this,
                            OrderResult.INSUFFICIENT_OWNER_BALANCE);
            return;
        }

        // Check stock
        int stock = InventoryUtils.getAmountOf(player.getInventory(), this.item
        );

        if (stock < this.amount) {
            ShopGUI.displayResult(player, this,
                    OrderResult.NOT_ENOUGH_ITEMS);
            return;
        }

        ItemcasePostTransactionEvent event = new ItemcasePostTransactionEvent(
                itemcase, player, this, OrderMode.SELL);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ShopGUI.displayResult(player, this, OrderResult.CANCELED);
            return;
        }

        // Add funds
        if (!this.itemcase.isInfinite()) {
            EconomyResponse ownerResponse = Vault.getEconomy().depositPlayer(
                    Bukkit.
                    getOfflinePlayer(player.getDisplayName()),
                    player.getWorld().getName(), amountDue);

            if (!ownerResponse.transactionSuccess()) {
                ShopGUI.displayResult(player, this,
                        OrderResult.TRANSACTION_FAILED);
                return;
            }
        }

        // Remove owner funds
        EconomyResponse response = Vault.getEconomy().withdrawPlayer(Bukkit.
                getOfflinePlayer(this.itemcase.getOwnerName()),
                player.getWorld().getName(), amountDue);

        if (!response.transactionSuccess()) {
            ShopGUI.displayResult(player, this, OrderResult.TRANSACTION_FAILED);
            return;
        }

        // Add owner stock
        if (!this.itemcase.isInfinite()) {
            ItemStack itemstack = this.item.clone();
            itemstack.setAmount(this.amount);

            this.itemcase.getInventory().addItem(itemstack);
        }

        // Remove player stock
        ItemStack itemstack = this.item.clone();
        itemstack.setAmount(this.amount);

        player.getInventory().removeItem(itemstack);

        ShopGUI.displayResult(player, this,
                OrderResult.SELL_SUCCESS);
        return;
    }

    /**
     * Cancel order.
     */
    public void cancel() {
        if (Bukkit.getScheduler().isQueued(this.taskID)) {
            Bukkit.getScheduler().cancelTask(this.taskID);
        }
    }

    /**
     * @return Itemcase involved.
     */
    public Itemcase getItemcase() {
        return itemcase;
    }

    /**
     * @return Amount of items to be bought/sold.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Update order.
     *
     * @param amount Amount of items to be bought/sold.
     * @param mode OrderMode.
     */
    public void update(int amount, OrderMode mode) {
        this.amount = amount;
        this.mode = mode;

        if (mode == OrderMode.BUY) {
            this.price = this.itemcase.getBuyPrice() * this.amount;
        } else if (mode == OrderMode.SELL) {
            this.price = this.itemcase.getSellPrice() * this.amount;
        }
    }

    /**
     * @return ItemStack being bought/sold.
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * @return OrderMode of order. (Maybe null)
     */
    public OrderMode getMode() {
        return this.mode;
    }

    /**
     * @return Price of order. (Maybe 0)
     */
    public double getPrice() {
        return this.price;
    }
}
