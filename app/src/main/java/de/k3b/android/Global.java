/*
 * Copyright (C) 2021 k3b
 *
 * This file is part of calef (calendar entry formatter) https://github.com/k3b/calef/ .
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package de.k3b.android;

import java.util.Locale;

/**
 * Global settings
 */
public class Global {
    /**
     * #6: local settings: which language should the gui use
     */
    public static final String PREF_KEY_USER_LOCALE = "user_locale";

    public static final String LOG_CONTEXT = "calef";
    /**
     * true: addToCompressQue several Log.d(...) to show what is going on.
     * debugEnabled is updated by the SettingsActivity
     */
    public static boolean debugEnabled = false;

    /**
     * Remember initial language settings. This allows setting "switch back to device language" after changing app locale
     */
    public static Locale systemLocale = Locale.getDefault();
}
