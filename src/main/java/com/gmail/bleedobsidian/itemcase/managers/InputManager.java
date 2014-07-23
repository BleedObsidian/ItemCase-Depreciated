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

import com.gmail.bleedobsidian.itemcase.managers.interfaces.InputListener;
import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 * A manager to handle input from players in chat. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class InputManager {

    /**
     * Pending inputs.
     */
    private final HashMap<Player, InputListener> pendingInputs = new HashMap<Player, InputListener>();

    /**
     * Add pending input listener.
     *
     * @param player Player.
     * @param listener Listener to call upon when player provides input.
     */
    public void addPendingInput(Player player, InputListener listener) {
        this.pendingInputs.put(player, listener);
    }

    /**
     * Remove pending input listener.
     *
     * @param player Player.
     */
    public void removePendingInput(Player player) {
        this.pendingInputs.remove(player);
    }

    /**
     * Set the input of pending input.
     *
     * @param player Player.
     * @param input Given input string.
     */
    public void setPendingInput(Player player, String input) {
        InputListener listener = this.pendingInputs.get(player);
        this.removePendingInput(player);

        listener.onPlayerInput(player, input);
    }

    /**
     * @param player Player.
     * @return If awaiting for input from a given player.
     */
    public boolean isPendingInput(Player player) {
        return this.pendingInputs.containsKey(player);
    }
}
