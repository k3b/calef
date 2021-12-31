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
import android.view.Menu;
import android.view.MenuItem;

import de.k3b.android.Global;
import de.k3b.android.widget.LocalizedActivity;

/**
 * show settings/config activity. On Start and Exit checks if data is valid.
 */
public class SettingsActivity extends PreferenceActivity {
    private SharedPreferences prefsInstance = null;
    private ListPreference defaultLocalePreference;  // #6: Support to change locale at runtime

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        LocalizedActivity.fixLocale(this);    // #6: Support to change locale at runtime
        super.onCreate(savedInstanceState);
        SettingsImpl.init(this);
        setResult(RESULT_CANCELED, null);
        this.addPreferencesFromResource(R.xml.preferences);

        prefsInstance = PreferenceManager
                .getDefaultSharedPreferences(this);
        // #6: Support to change locale at runtime
        defaultLocalePreference =
                (ListPreference) findPreference(Global.PREF_KEY_USER_LOCALE);
        defaultLocalePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setLanguage((String) newValue);
                LocalizedActivity.recreate(SettingsActivity.this);
                return true; // change is allowed
            }
        });

        // #6: Support to change locale at runtime
        updateSummary();
    }

    // #6: Support to change locale at runtime
    // This is used to show the status of some preference in the description
    private void updateSummary() {
        final String languageKey = prefsInstance.getString(Global.PREF_KEY_USER_LOCALE, "");
        setLanguage(languageKey);
    }

    // #6: Support to change locale at runtime
    private void setLanguage(String languageKey) {
        setPref(languageKey, defaultLocalePreference, R.array.pref_locale_names);
    }

    private void setPref(String key, ListPreference listPreference, int arrayResourceId) {
        int index = listPreference.findIndexOfValue(key);
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
