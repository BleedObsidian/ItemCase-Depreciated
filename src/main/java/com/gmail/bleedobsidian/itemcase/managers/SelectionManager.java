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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.gmail.bleedobsidian.itemcase.managers.interfaces.SelectionListener;
import com.gmail.bleedobsidian.itemcase.managers.itemcase.Itemcase;

public class SelectionManager {
    private Map<Player, SelectionListener> pending = new HashMap<Player, SelectionListener>();

    public void call(Player player, Itemcase itemcase) {
        if (pending.containsKey(player)) {
            pending.get(player).selected(player, itemcase);
            pending.remove(player);
        }
    }

    public void addPendingSelection(SelectionListener listener, Player player) {
        this.pending.put(player, listener);
    }

    public void removePendingSelection(SelectionListener listener, Player player) {
        this.pending.remove(player);
    }
}
