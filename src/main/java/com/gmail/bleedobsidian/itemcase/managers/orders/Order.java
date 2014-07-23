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
     * Amount.
     */
    private int amount = 1;

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
                        OrderResult.TRANSACTION_FAILED, 0);
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
                    displayResult(player, this, OrderResult.INSUFFICIENT_BALANCE,
                            0);
            return;
        }

        // Check stock
        if (!this.itemcase.isInfinite()) {
            int stock = InventoryUtils.getAmountOf(this.itemcase.getInventory(),
                    this.item);

            if (stock < this.amount) {
                ShopGUI.displayResult(player, this,
                        OrderResult.INSUFFICIENT_STOCK, 0);
                return;
            }
        }

        // Remove funds
        EconomyResponse response = Vault.getEconomy().withdrawPlayer(Bukkit.
                getOfflinePlayer(player.getUniqueId()),
                player.getWorld().getName(), amountDue);

        if (!response.transactionSuccess()) {
            ShopGUI.displayResult(player, this, OrderResult.TRANSACTION_FAILED,
                    0);
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
                        OrderResult.TRANSACTION_FAILED, 0);
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
                OrderResult.BUY_SUCCESS, amountDue);
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
                        OrderResult.TRANSACTION_FAILED, 0);
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
                            OrderResult.INSUFFICIENT_OWNER_BALANCE,
                            0);
            return;
        }

        // Check stock
        int stock = InventoryUtils.getAmountOf(player.getInventory(), this.item
        );

        if (stock < this.amount) {
            ShopGUI.displayResult(player, this,
                    OrderResult.NOT_ENOUGH_ITEMS, 0);
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
                        OrderResult.TRANSACTION_FAILED, 0);
                return;
            }
        }

        // Remove owner funds
        EconomyResponse response = Vault.getEconomy().withdrawPlayer(Bukkit.
                getOfflinePlayer(this.itemcase.getOwnerName()),
                player.getWorld().getName(), amountDue);

        if (!response.transactionSuccess()) {
            ShopGUI.displayResult(player, this, OrderResult.TRANSACTION_FAILED,
                    0);
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
                OrderResult.SELL_SUCCESS, amountDue);
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
     * @param amount Amount of items to be bought/sold.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return ItemStack being bought/sold.
     */
    public ItemStack getItem() {
        return item;
    }
}
