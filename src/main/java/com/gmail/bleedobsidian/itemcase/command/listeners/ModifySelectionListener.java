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

import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.ItemCase;
import com.gmail.bleedobsidian.itemcase.command.commands.ModifyCommand;
import com.gmail.bleedobsidian.itemcase.managers.interfaces.SelectionListener;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.ItemcaseType;

public class ModifySelectionListener implements SelectionListener {
    private final ItemCase plugin;
    private ItemcaseType type;
    private final String[] args;

    public ModifySelectionListener(ItemCase plugin, ItemcaseType type,
            String[] args) {
        this.plugin = plugin;
        this.type = type;
        this.args = args;
    }

    public ModifySelectionListener(ItemCase plugin, String[] args) {
        this.plugin = plugin;
        this.args = args;
    }

    public void selected(Player player, Itemcase itemcase) {
        if (this.type != null) {
            ModifyCommand.selected(plugin, player, args, itemcase, type);
        } else {
            ModifyCommand.selectedInfinite(plugin, player, args, itemcase);
        }
    }
}
