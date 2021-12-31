/*
 * Copyright (C) 2021 k3b
 *
 * This file is part of CalEF (Calendar Entry Formatter) https://github.com/k3b/calef/ .
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
package de.k3b.android.calef;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.k3b.android.Global;
import de.k3b.calef.CalendarFormatter;

/**
 * implements SettingsData from android preferences
 */
public class SettingsImpl {
    private SettingsImpl() {
    }

    /**
     * Load values from prefs. return true, if zip output dir is writable
     */
    public static boolean init(final Context context) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Global.debugEnabled = SettingsImpl.getPrefValue(prefs,
                "isDebugEnabled", Global.debugEnabled);
        CalendarFormatter.debugEnabled = Global.debugEnabled;

        return true;
    }

    /**
     * sets preference value
     */
    private static void setValue(SharedPreferences prefs, String key, String value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

    /**
     * Since this value comes from a text-editor it is stored as string.
     * Conversion to int must be done yourself.
     */
    private static int getPrefValue(final SharedPreferences prefs,
                                    final String key, final int notFoundValue) {
        try {
            return Integer.parseInt(prefs.getString(key,
                    Integer.toString(notFoundValue)));
        } catch (final Exception ex) {
            // ClassCastException or NumberFormatException

            Log.w(Global.LOG_CONTEXT, "getPrefValue-Integer(" + key + ","
                    + notFoundValue + ") failed: " + ex.getMessage());
            return notFoundValue;
        }
    }

    private static boolean getPrefValue(final SharedPreferences prefs,
                                        final String key, final boolean notFoundValue) {
        try {
            return prefs.getBoolean(key, notFoundValue);
        } catch (final Exception ex) {
            // ClassCastException or FormatException
            Log.w(Global.LOG_CONTEXT, "getPrefValue-Boolean(" + key + ","
                    + notFoundValue + ") failed: " + ex.getMessage());
            return notFoundValue;
        }
    }

    private static String getPrefValue(final SharedPreferences prefs,
                                       final String key, final String notFoundValue) {
        String result = prefs.getString(key, null);

        if ((result == null) || (result.trim().length() == 0)) {
            result = notFoundValue;
            setValue(prefs, key, notFoundValue);
        }
        return result;
    }

}
