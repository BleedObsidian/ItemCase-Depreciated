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
package com.gmail.bleedobsidian.itemcase.command.listeners;

import com.gmail.bleedobsidian.itemcase.command.commands.ModifyCommand;
import com.gmail.bleedobsidian.itemcase.managers.interfaces.SelectionListener;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;
import org.bukkit.entity.Player;

/**
 * Modify Command Selection Listener. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class ModifySelectionListener implements SelectionListener {

    /**
     * ItemcaseType.
     */
    private ItemcaseType type;

    /**
     * Command arguments.
     */
    private final String[] args;

    /**
     * New ModifySelectionListener.
     *
     * @param type ItemcaseType.
     * @param args Command arguments.
     */
    public ModifySelectionListener(ItemcaseType type,
            String[] args) {
        this.type = type;
        this.args = args;
    }

    /**
     * New ModifySelectionListener.
     *
     * @param args Command arguments.
     */
    public ModifySelectionListener(String[] args) {
        this.args = args;
    }

    /**
     * On selection.
     */
    public void selected(Player player, Itemcase itemcase) {
        if (this.type != null) {
            ModifyCommand.selected(player, args, itemcase, type);
        } else {
            ModifyCommand.selectedInfinite(player, args, itemcase);
        }
    }
}
