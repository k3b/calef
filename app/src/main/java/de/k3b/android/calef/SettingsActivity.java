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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.Locale;

import de.k3b.android.Global;
import de.k3b.android.widget.LocalizedActivity;
import de.k3b.calef.CalendarFormatter;

/**
 * show settings/config activity. On Start and Exit checks if data is valid.
 */
public class SettingsActivity extends PreferenceActivity {
    private SharedPreferences prefsInstance = null;
    private ListPreference prefLocale;  // Support to change locale at runtime
    private ListPreference prefDay;
    private ListPreference prefDate;
    private ListPreference prefTime;
    private Preference prefExample;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        LocalizedActivity.fixLocale(this);    // #6: Support to change locale at runtime
        super.onCreate(savedInstanceState);
        SettingsImpl.init(this);
        setResult(RESULT_CANCELED, null);
        this.addPreferencesFromResource(R.xml.preferences);

        prefsInstance = PreferenceManager
                .getDefaultSharedPreferences(this);

        prefLocale = (ListPreference) findPreference(Global.PREF_KEY_USER_LOCALE);
        prefDay = (ListPreference) findPreference(SettingsImpl.PREF_MODE_DAY);
        prefDate = (ListPreference) findPreference(SettingsImpl.PREF_MODE_DATE);
        prefTime = (ListPreference) findPreference(SettingsImpl.PREF_MODE_TIME);
        prefExample = findPreference("mode_example");

        prefLocale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setLanguage((String) newValue);
                LocalizedActivity.recreate(SettingsActivity.this);
                return true; // change is allowed
            }
        });

        Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (Global.debugEnabled) {
                    Log.i(Global.LOG_CONTEXT, "Update from " + preference.getTitle());
                }
                updateSummary((ListPreference) preference, (String) newValue);
                return true; // change is allowed
            }
        };

        prefDay.setOnPreferenceChangeListener(onPreferenceChangeListener);
        prefDate.setOnPreferenceChangeListener(onPreferenceChangeListener);
        prefTime.setOnPreferenceChangeListener(onPreferenceChangeListener);
        showValues();
    }

    private void showValues() {
        setLanguage(prefLocale.getValue());
        setPref(prefDay.getValue(), prefDay, R.array.pref_mode_names);
        setPref(prefDate.getValue(), prefDate, R.array.pref_mode_names);
        setPref(prefTime.getValue(), prefTime, R.array.pref_mode_names);
        updateSummary(null, null);
    }

    // This is used to show the status of some preference in the description
    private void updateSummary(ListPreference preference, String newValue) {
        // setLanguage(languageKey);
        if (preference != null) {
            preference.setValue(newValue);
            setPref(newValue, preference, R.array.pref_mode_names);
        }

        CalendarFormatter formatter = new CalendarFormatter(
                Locale.getDefault(),
                getInt(prefDay, CalendarFormatter.STYLE.MEDIUM),
                getInt(prefDate, CalendarFormatter.STYLE.SHORT),
                getInt(prefTime, CalendarFormatter.STYLE.SHORT));
        if (Global.debugEnabled) {
            Log.i(Global.LOG_CONTEXT, "updateSummary " + Locale.getDefault() +
                    ", " + getInt(prefDay, CalendarFormatter.STYLE.MEDIUM) +
                    ", " + getInt(prefDate, CalendarFormatter.STYLE.SHORT) +
                    ", " + getInt(prefTime, CalendarFormatter.STYLE.SHORT) +
                    "");
        }

        prefExample.setSummary(formatter.add(new StringBuilder(), new Date(), null, null));
    }

    private int getInt(ListPreference prefDay, int defaultValue) {
        String strValue = (prefDay == null) ? null : prefDay.getValue();
        if (strValue != null && !strValue.isEmpty()) {
            try {
                return Integer.parseInt(strValue);
            } catch (Throwable ignore) {
            }
        }
        return defaultValue;
    }

    // #6: Support to change locale at runtime
    private void setLanguage(String languageKey) {
        setPref(languageKey, prefLocale, R.array.pref_locale_names);
    }

    private void setPref(String value, ListPreference listPreference, int arrayResourceId) {
        int index = listPreference.findIndexOfValue(value);
        String summary = "";

        if (index >= 0) {
            String[] names = this.getResources().getStringArray(arrayResourceId);
            if (index < names.length) {
                summary = names[index];
            }
        }
        listPreference.setSummary(summary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.menu_config, menu);
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
