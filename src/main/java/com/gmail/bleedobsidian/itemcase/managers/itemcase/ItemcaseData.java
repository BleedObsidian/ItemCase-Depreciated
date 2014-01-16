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

package com.gmail.bleedobsidian.itemcase.managers.itemcase;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.gmail.bleedobsidian.itemcase.ItemCase;

public class ItemcaseData implements MetadataValue {
    public boolean asBoolean() {
        return true;
    }

    public byte asByte() {
        return 0;
    }

    public double asDouble() {
        return 0;
    }

    public float asFloat() {
        return 0;
    }

    public int asInt() {
        return 0;
    }

    public long asLong() {
        return 0;
    }

    public short asShort() {
        return 0;
    }

    public String asString() {
        return null;
    }

    public Plugin getOwningPlugin() {
        return ItemCase.getInstance();
    }

    public void invalidate() {
    }

    public Object value() {
        return null;
    }

}
