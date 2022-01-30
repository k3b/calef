/*
 * Copyright (C) 2021-2022 k3b
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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;

import de.k3b.android.Global;
import de.k3b.calef.CalendarFormatter;

/**
 * implements SettingsData from android preferences
 */
public class SettingsImpl {
    public static final String PREF_MODE_DAY = "mode_day";
    public static final String PREF_MODE_DATE = "mode_date";
    public static final String PREF_MODE_TIME = "mode_time";

    public static final String PREF_MODE_MESSAGE = "mode_message";
    public static final String PREF_MESSAGE_PREFIX = "mode_message_prefix";
    private static final String LAST_CALFILE_NAME = "example.ics";

    private SettingsImpl() {
    }

    /**
     * Load values from prefs. return current formatter
     */
    public static CalendarFormatter init(final Context context) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Global.debugEnabled = SettingsImpl.getPrefValue(prefs,
                "isDebugEnabled", Global.debugEnabled);
        CalendarFormatter.debugEnabled = Global.debugEnabled;

        int messageMode = getPrefValue(prefs, SettingsImpl.PREF_MODE_MESSAGE, CalendarFormatter.STYLE.LONG);
        boolean addDetails = messageMode == CalendarFormatter.STYLE.FULL || messageMode == CalendarFormatter.STYLE.LONG;
        String messagePrefix = null;
        if (messageMode == CalendarFormatter.STYLE.FULL || messageMode == CalendarFormatter.STYLE.MEDIUM) {
            messagePrefix = SettingsImpl.getMessagePrefix(context, prefs);
        }

        CalendarFormatter formatter = new CalendarFormatter(
                Locale.getDefault(),
                getPrefValue(prefs, SettingsImpl.PREF_MODE_DAY, CalendarFormatter.STYLE.MEDIUM),
                getPrefValue(prefs, SettingsImpl.PREF_MODE_DATE, CalendarFormatter.STYLE.SHORT),
                getPrefValue(prefs, SettingsImpl.PREF_MODE_TIME, CalendarFormatter.STYLE.SHORT), messagePrefix, addDetails);

        return formatter;
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

    public static Calendar getLast(Context context) {
        try (InputStream in = context.openFileInput(LAST_CALFILE_NAME)) {
            return new CalendarBuilder().build(in);
        } catch (Throwable ignore) {
            try (InputStream in = context.getAssets().open(LAST_CALFILE_NAME)) {
                return new CalendarBuilder().build(in);
            } catch (Throwable ignore2) {
            }
        }
        return null;
    }

    public static void putLast(Context context, Calendar cal) {
        context.deleteFile(LAST_CALFILE_NAME);
        if (cal != null) {
            try (PrintStream out = new PrintStream(context.openFileOutput(LAST_CALFILE_NAME, Context.MODE_PRIVATE))) {
                out.println(cal.toString());
            } catch (Throwable ignore) {
            }
        }
    }

    public static String getMessagePrefix(Context context, final SharedPreferences prefs) {
        String result = getPrefValue(prefs, PREF_MESSAGE_PREFIX, null);
        if (result == null) {
            result = getDefaultPrefix(context);
        }
        return result;
    }

    public static String setMessagePrefix(Context context, final SharedPreferences prefs, String value) {
        if (value != null && getDefaultPrefix(context).compareToIgnoreCase(value) == 0) {
            value = null;
        }

        if (value == null || value.isEmpty()) {
            prefs.edit().remove(PREF_MESSAGE_PREFIX).apply();
            value = null;
        } else {
            prefs.edit().putString(PREF_MESSAGE_PREFIX, value).apply();
        }
        return value;
    }

    private static String getDefaultPrefix(Context context) {
        return context.getString(R.string.settings_message_prefix_default);
    }

}
