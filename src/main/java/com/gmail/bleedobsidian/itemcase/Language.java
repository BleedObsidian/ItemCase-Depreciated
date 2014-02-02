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

package com.gmail.bleedobsidian.itemcase;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;

public class Language {
    private static LanguageFile languageFile;
    private static String language;

    public static void setLangauge(String language, JavaPlugin plugin) {
        Language.language = language;
        Language.languageFile = new LanguageFile(language);

        Language.languageFile.load(plugin);
    }

    public static boolean exists(String language) {
        if (language.equals("en-us")) {
            return true;
        } else if (language.equals("de-de")) {
            return true;
        } else {
            return false;
        }
    }

    public static LanguageFile getLanguageFile() {
        return Language.languageFile;
    }

    public static String getLanguage() {
        return Language.language;
    }
}
