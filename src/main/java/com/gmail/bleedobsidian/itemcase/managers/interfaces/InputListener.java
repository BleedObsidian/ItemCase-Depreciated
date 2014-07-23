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
package com.gmail.bleedobsidian.itemcase.managers.interfaces;

import org.bukkit.entity.Player;

/**
 * An interface listener for the InputManager that is called upon when the
 * player provides input.
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public interface InputListener {

    /**
     * Fired when the player sets the amount.
     *
     * @param player Player that set amount.
     * @param input Input that player provided.
     */
    public void onPlayerInput(Player player, String input);
}
