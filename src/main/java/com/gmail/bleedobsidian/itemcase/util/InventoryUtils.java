package com.gmail.bleedobsidian.itemcase.util;

import java.util.ListIterator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A utility class for Inventory related methods.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class InventoryUtils {

    /**
     * Get amount of ItemStack items in inventory.
     *
     * @param inventory Inventory.
     * @param itemStack ItemStack to count.
     * @return Amount of items.
     */
    public static int getAmountOf(Inventory inventory, ItemStack itemStack) {
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
