package com.gmail.bleedobsidian.itemcase.util;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.Language;
import com.gmail.bleedobsidian.itemcase.Vault;
import com.gmail.bleedobsidian.itemcase.loggers.PlayerLogger;
import com.gmail.bleedobsidian.itemcase.managers.interfaces.InputListener;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.orders.Order;
import com.gmail.bleedobsidian.itemcase.managers.orders.OrderMode;
import com.gmail.bleedobsidian.itemcase.managers.orders.OrderResult;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

/**
 * A utility class for display the shop GUI to players.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ShopGUI {

    /**
     * DecimalFormat to display currencies with.
     */
    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");

    /**
     * Display buy GUI to given player.
     *
     * @param itemcase Itemcase involved.
     * @param player Player.
     * @param order Current order.
     */
    public static void displayGUI(final Itemcase itemcase, final Player player,
            final Order order) {
        // Header
        PlayerLogger.messageLanguage(player, "Player.ItemCase.Shop-Message1");

        if (!itemcase.isInfinite()) {
            PlayerLogger.message(player, Language.getLanguageFile().getMessage(
                    "Player.ItemCase.Shop-Message-Owner",
                    new String[]{"%Player%", itemcase.getOwnerName()}));
        }

        // Custom item name
        String itemName = itemcase.getItemStack().getType().name();
        if (itemcase.getItemStack().hasItemMeta()) {
            if (itemcase.getItemStack().getItemMeta().getDisplayName() != null) {
                itemName = itemcase.getItemStack().getItemMeta().
                        getDisplayName();
            }
        }

        // Item name
        PlayerLogger.message(player, Language.getLanguageFile().getMessage(
                "Player.ItemCase.Shop-Message2",
                new String[]{"%Item%", itemName}));

        // Item Enchantments
        if (itemcase.getItemStack().getItemMeta().hasEnchants()) {
            String enchantments = "";

            for (Entry<Enchantment, Integer> enchantment : itemcase.
                    getItemStack().getEnchantments().entrySet()) {
                enchantments += enchantment.getKey().getName() + " (L" + enchantment.
                        getValue() + "), ";
            }

            PlayerLogger.message(player, Language.getLanguageFile().getMessage(
                    "Player.ItemCase.Shop-Message3",
                    new String[]{"%Enchantments%", enchantments}));
        }

        // Item Buy Price
        if (itemcase.canBuy()) {
            if (itemcase.getBuyPrice() > 1) {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.ItemCase.Shop-Message5",
                                new String[]{"%Cost%", "" + itemcase.
                                    getBuyPrice(), "%Currency%", Vault.
                                    getEconomy().currencyNamePlural()}));
            } else {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.ItemCase.Shop-Message5",
                                new String[]{"%Cost%", "" + itemcase.
                                    getBuyPrice(), "%Currency%", Vault.
                                    getEconomy().currencyNameSingular()}));
            }
        }

        // Item Sell Price
        if (itemcase.canSell()) {
            if (itemcase.getSellPrice() > 1) {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.ItemCase.Shop-Message6",
                                new String[]{"%Cost%", "" + itemcase.
                                    getSellPrice(), "%Currency%", Vault.
                                    getEconomy().currencyNamePlural()}));
            } else {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.ItemCase.Shop-Message6",
                                new String[]{"%Cost%", "" + itemcase.
                                    getSellPrice(), "%Currency%", Vault.
                                    getEconomy().currencyNameSingular()}));
            }
        }

        // Footer
        PlayerLogger.messageLanguage(player, "Player.Order.End");

        // Buy or Sell
        PlayerLogger.messageLanguage(player, "Player.ItemCase.Shop-Message4");

        InputListener inputListener = new InputListener() {

            @Override
            public void onPlayerInput(Player player, String input) {
                if (input.equalsIgnoreCase("buy") || input.equalsIgnoreCase(
                        "b")) {
                    if (!player.hasPermission("itemcase.buy")) {
                        PlayerLogger.messageLanguage(player,
                                "Player.ItemCase.Permission-Buy");
                        PlayerLogger.messageLanguage(player, "Player.Order.End");
                        order.cancel();
                        ItemCase.getInstance().getShopManager().
                                removePendingOrder(player);
                        return;
                    }

                    if (itemcase.canBuy()) {
                        PlayerLogger.message(player, Language.getLanguageFile().
                                getMessage("Player.Order.Mode-Set",
                                        new String[]{"%Mode%", "BUY"}));
                        PlayerLogger.messageLanguage(player, "Player.Order.End");

                        ShopGUI.displayBuyGUI(itemcase, player, order);
                        return;
                    } else {
                        PlayerLogger.messageLanguage(player,
                                "Player.ItemCase.No-Buy");
                        PlayerLogger.messageLanguage(player, "Player.Order.End");
                        order.cancel();
                        ItemCase.getInstance().getShopManager().
                                removePendingOrder(player);
                        return;
                    }
                } else if (input.equalsIgnoreCase("sell") || input.
                        equalsIgnoreCase(
                                "s")) {
                    if (!player.hasPermission("itemcase.sell")) {
                        PlayerLogger.messageLanguage(player,
                                "Player.ItemCase.Permission-Sell");
                        PlayerLogger.messageLanguage(player, "Player.Order.End");
                        order.cancel();
                        ItemCase.getInstance().getShopManager().
                                removePendingOrder(player);
                        return;
                    }

                    if (itemcase.canSell()) {
                        PlayerLogger.message(player, Language.getLanguageFile().
                                getMessage("Player.Order.Mode-Set",
                                        new String[]{"%Mode%", "SELL"}));
                        PlayerLogger.messageLanguage(player, "Player.Order.End");

                        ShopGUI.displaySellGUI(itemcase, player, order);
                        return;
                    } else {
                        PlayerLogger.messageLanguage(player,
                                "Player.ItemCase.No-Sell");
                        PlayerLogger.messageLanguage(player, "Player.Order.End");
                        order.cancel();
                        ItemCase.getInstance().getShopManager().
                                removePendingOrder(player);
                        return;
                    }
                } else {
                    PlayerLogger.messageLanguage(player,
                            "Player.ItemCase.Invalid-Option");
                    PlayerLogger.messageLanguage(player, "Player.Order.End");
                    order.cancel();
                    ItemCase.getInstance().getShopManager().
                            removePendingOrder(player);
                    return;
                }
            }
        };

        ItemCase.getInstance().getInputManager().addPendingInput(player,
                inputListener);
    }

    /**
     * Display buy GUI to given player.
     *
     * @param itemcase Itemcase involved.
     * @param player Player.
     * @param order Current order.
     */
    public static void displayBuyGUI(Itemcase itemcase, Player player,
            final Order order) {
        // Amount
        PlayerLogger.messageLanguage(player, "Player.ItemCase.Shop-Message7");

        InputListener inputListener = new InputListener() {

            @Override
            public void onPlayerInput(Player player, String input) {
                int amount = 0;

                try {
                    amount = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    PlayerLogger.messageLanguage(player,
                            "Player.Order.Amount-Error1");
                    PlayerLogger.messageLanguage(player, "Player.Order.End");
                    order.cancel();
                    ItemCase.getInstance().getShopManager().
                            removePendingOrder(player);
                    return;
                }

                if (amount <= 0) {
                    PlayerLogger.messageLanguage(player,
                            "Player.Order.Amount-Error2");
                    PlayerLogger.messageLanguage(player, "Player.Order.End");
                    order.cancel();
                    ItemCase.getInstance().getShopManager().
                            removePendingOrder(player);
                    return;
                }

                order.update(amount, OrderMode.BUY);

                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage("Player.Order.Amount-Set",
                                new String[]{"%Amount%", "" + amount}));
                PlayerLogger.messageLanguage(player, "Player.Order.End");

                order.buy(player);
                order.cancel();
                ItemCase.getInstance().getShopManager().
                        removePendingOrder(player);

            }
        };

        ItemCase.getInstance().getInputManager().addPendingInput(player,
                inputListener);
    }

    /**
     * Display sell GUI to given player.
     *
     * @param itemcase Itemcase involved.
     * @param player Player.
     * @param order Current order.
     */
    public static void displaySellGUI(Itemcase itemcase, Player player,
            final Order order) {
        // Amount
        PlayerLogger.messageLanguage(player, "Player.ItemCase.Shop-Message8");

        InputListener inputListener = new InputListener() {

            @Override
            public void onPlayerInput(Player player, String input) {
                int amount = 0;

                try {
                    amount = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    PlayerLogger.messageLanguage(player,
                            "Player.Order.Amount-Error1");
                    PlayerLogger.messageLanguage(player, "Player.Order.End");
                    order.cancel();
                    ItemCase.getInstance().getShopManager().
                            removePendingOrder(player);
                    return;
                }

                if (amount <= 0) {
                    PlayerLogger.messageLanguage(player,
                            "Player.Order.Amount-Error2");
                    PlayerLogger.messageLanguage(player, "Player.Order.End");
                    order.cancel();
                    ItemCase.getInstance().getShopManager().
                            removePendingOrder(player);
                    return;
                }

                order.update(amount, OrderMode.SELL);

                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage("Player.Order.Amount-Set",
                                new String[]{"%Amount%", "" + amount}));
                PlayerLogger.messageLanguage(player, "Player.Order.End");

                order.sell(player);
                order.cancel();
                ItemCase.getInstance().getShopManager().
                        removePendingOrder(player);

            }
        };

        ItemCase.getInstance().getInputManager().addPendingInput(player,
                inputListener);
    }

    /**
     * Display order result GUI.
     *
     * @param player Player.
     * @param order Order.
     * @param result OrderResult.
     */
    public static void displayResult(Player player, Order order,
            OrderResult result) {
        double price = order.getPrice();

        if (result == OrderResult.BUY_SUCCESS) {
            if (price <= 1) {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Withdraw",
                                new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                    getEconomy().currencyNameSingular()}));
            } else {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Withdraw",
                                new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                    getEconomy().currencyNamePlural()}));
            }

            if (order.getItem().hasItemMeta() && order.getItem().getItemMeta().
                    getDisplayName() != null) {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Bought-Items",
                                new String[]{"%Amount%", "" + order.getAmount(), "%Item%", order.
                                    getItem().getItemMeta().getDisplayName()}));
            } else {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Bought-Items",
                                new String[]{"%Amount%", "" + order.getAmount(), "%Item%", order.
                                    getItem().getType().name()}));
            }

            PlayerLogger.messageLanguage(player, "Player.Order.End");

            if (!order.getItemcase().isInfinite()) {
                OfflinePlayer ownerOffline = Bukkit.getOfflinePlayer(order.
                        getItemcase().getOwnerName());

                if (!ownerOffline.getName().equals(player.getName())) {
                    if (ownerOffline.isOnline()) {
                        Player owner = Bukkit.getPlayer(order.getItemcase().
                                getOwnerName());

                        if (order.getItem().hasItemMeta() && order.getItem().
                                getItemMeta().
                                getDisplayName() != null) {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Owner-Buy",
                                            new String[]{"%Player%", player.
                                                getName(), "%Amount%", "" + order.
                                                getAmount(), "%Item%", order.
                                                getItem().getItemMeta().
                                                getDisplayName()}));
                        } else {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Owner-Buy",
                                            new String[]{"%Player%", player.
                                                getName(), "%Amount%", "" + order.
                                                getAmount(), "%Item%", order.
                                                getItem().getType().name()}));
                        }

                        if (price <= 1) {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Deposit",
                                            new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                                getEconomy().
                                                currencyNameSingular()}));
                        } else {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Deposit",
                                            new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                                getEconomy().
                                                currencyNamePlural()}));
                        }
                    }
                }
            }
        } else if (result == OrderResult.SELL_SUCCESS) {
            if (price <= 1) {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Deposit",
                                new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                    getEconomy().currencyNameSingular()}));
            } else {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Deposit",
                                new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                    getEconomy().currencyNamePlural()}));
            }

            if (order.getItem().hasItemMeta() && order.getItem().getItemMeta().
                    getDisplayName() != null) {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Sold-Items",
                                new String[]{"%Amount%", "" + order.getAmount(), "%Item%", order.
                                    getItem().getItemMeta().getDisplayName()}));
            } else {
                PlayerLogger.message(player, Language.getLanguageFile().
                        getMessage(
                                "Player.Order.Sold-Items",
                                new String[]{"%Amount%", "" + order.getAmount(), "%Item%", order.
                                    getItem().getType().name()}));
            }

            PlayerLogger.messageLanguage(player, "Player.Order.End");

            if (!order.getItemcase().isInfinite()) {
                OfflinePlayer ownerOffline = Bukkit.getOfflinePlayer(order.
                        getItemcase().getOwnerName());

                if (!ownerOffline.getName().equals(player.getName())) {
                    if (ownerOffline.isOnline()) {
                        Player owner = Bukkit.getPlayer(order.getItemcase().
                                getOwnerName());

                        if (order.getItem().hasItemMeta() && order.getItem().
                                getItemMeta().
                                getDisplayName() != null) {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Owner-Sell",
                                            new String[]{"%Player%", player.
                                                getName(), "%Amount%", "" + order.
                                                getAmount(), "%Item%", order.
                                                getItem().getItemMeta().
                                                getDisplayName()}));
                        } else {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Owner-Sell",
                                            new String[]{"%Player%", player.
                                                getName(), "%Amount%", "" + order.
                                                getAmount(), "%Item%", order.
                                                getItem().getType().name()}));
                        }

                        if (price <= 1) {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Withdraw",
                                            new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                                getEconomy().
                                                currencyNameSingular()}));
                        } else {
                            PlayerLogger.message(owner, Language.
                                    getLanguageFile().
                                    getMessage(
                                            "Player.Order.Withdraw",
                                            new String[]{"%Amount%", "" + price, "%Currency%", Vault.
                                                getEconomy().
                                                currencyNamePlural()}));
                        }
                    }
                }
            }
        } else if (result == OrderResult.INSUFFICIENT_BALANCE) {
            PlayerLogger.messageLanguage(player, "Player.Order.Balance-Error");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
        } else if (result == OrderResult.INSUFFICIENT_STOCK) {
            PlayerLogger.messageLanguage(player, "Player.Order.No-Stock");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
        } else if (result == OrderResult.INSUFFICIENT_OWNER_BALANCE) {
            PlayerLogger.messageLanguage(player, "Player.Order.Owner-Balance");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
        } else if (result == OrderResult.NOT_ENOUGH_ITEMS) {
            PlayerLogger.messageLanguage(player, "Player.Order.Item-Error");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
        } else if (result == OrderResult.TRANSACTION_FAILED) {
            PlayerLogger.messageLanguage(player,
                    "Player.Order.Transaction-Error");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
        } else if (result == OrderResult.CANCELED) {
            PlayerLogger.messageLanguage(player, "Player.Order.Canceled");
            PlayerLogger.messageLanguage(player, "Player.Order.End");
            ItemCase.getInstance().getShopManager().removePendingOrder(player);
        }
    }

    /**
     * Display cancel order GUI.
     *
     * @param player Player cancelling order.
     */
    public static void displayCancelGUI(Player player) {
        PlayerLogger.message(
                player,
                Language.getLanguageFile().getMessage(
                        "Player.ItemCase.Start-Chat"));
    }
}
