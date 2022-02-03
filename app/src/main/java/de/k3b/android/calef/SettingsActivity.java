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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
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

    // pref elements in display order
    private ListPreference prefListLocale;  // Support to change locale at runtime
    private Preference prefExample;
    private ListPreference prefListDay;
    private ListPreference prefListDate;
    private ListPreference prefListTime;

    private ListPreference prefListMessage;
    private EditTextPreference prefMessagePrefix;
    private Preference prefLastExample;
    private Preference prefMessageResend;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        LocalizedActivity.fixLocale(this);    // #6: Support to change locale at runtime
        super.onCreate(savedInstanceState);
        SettingsImpl.init(this);
        setResult(RESULT_CANCELED, null);
        this.addPreferencesFromResource(R.xml.preferences);

        prefsInstance = PreferenceManager.getDefaultSharedPreferences(this);

        prefListLocale = (ListPreference) findPreference(Global.PREF_KEY_USER_LOCALE);
        prefListDay = (ListPreference) findPreference(SettingsImpl.PREF_MODE_DAY);
        prefListDate = (ListPreference) findPreference(SettingsImpl.PREF_MODE_DATE);
        prefListTime = (ListPreference) findPreference(SettingsImpl.PREF_MODE_TIME);
        prefListMessage = (ListPreference) findPreference(SettingsImpl.PREF_MODE_MESSAGE);

        prefExample = findPreference("mode_example");
        prefLastExample = findPreference("mode_last_example");
        prefMessagePrefix = (EditTextPreference) findPreference("mode_message_prefix");

        prefMessageResend = findPreference("mode_message_resend");

        Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (Global.debugEnabled) {
                    Log.i(Global.LOG_CONTEXT, "Update from " + preference.getTitle());
                }
                updateSummary(preference, (String) newValue);
                return true; // change is allowed
            }
        };
        prefListDay.setOnPreferenceChangeListener(onPreferenceChangeListener);
        prefListDate.setOnPreferenceChangeListener(onPreferenceChangeListener);
        prefListTime.setOnPreferenceChangeListener(onPreferenceChangeListener);
        prefListMessage.setOnPreferenceChangeListener(onPreferenceChangeListener);

        prefMessageResend.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    CalefActivity.sendResult(SettingsActivity.this, SettingsImpl.getLast(SettingsActivity.this));
                } catch (Exception ex) {
                    CalefActivity.toast(SettingsActivity.this, getString(R.string.error_cannot_convert_or_resend, "", ex.getMessage()));

                }
                return false;
            }
        });

        prefListLocale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setLanguage((String) newValue);
                LocalizedActivity.recreate(SettingsActivity.this);
                return true; // change is allowed
            }
        });

        prefMessagePrefix.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefMessagePrefix.setText((String) newValue);
                SettingsImpl.setMessagePrefix(SettingsActivity.this, prefsInstance, (String) newValue);
                showValues();
                return false; // value is already set
            }
        });

        prefExample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dumpExamples();
                return false;
            }
        });

        showValues();
    }

    private void dumpExamples() {
        Date xmas = new Date(2022 - 1900, 12 - 1, 24, 17, 20);
        String[] names = getResources().getStringArray(R.array.pref_locale_names);
        String[] locales = getResources().getStringArray(R.array.pref_locale_values);

        StringBuilder result = new StringBuilder();
        result.append("<table>");
        dumpTableRow(result, "Language", "Short", "Long");
        for (int i = 1; i < names.length; i++) {
            dump(result, xmas, names[i], locales[i]);
        }
        result.append("</table>");
        Log.i(CalefActivity.TAG, result.toString());
    }

    private void dump(StringBuilder result, Date date, String name, String localeName) {
        // i.e. "de" for german or "pt-BR" for portogeese in brasilia
        String[] languageParts = localeName.split("-");
        Locale locale = (languageParts.length == 1) ? new Locale(localeName) : new Locale(languageParts[0], languageParts[1]);

        CalendarFormatter formatterShort = new CalendarFormatter(locale, CalendarFormatter.STYLE.SHORT, CalendarFormatter.STYLE.SHORT, CalendarFormatter.STYLE.SHORT, "", false);
        CalendarFormatter formatterLong = new CalendarFormatter(locale, CalendarFormatter.STYLE.LONG, CalendarFormatter.STYLE.LONG, CalendarFormatter.STYLE.FULL, "", false);
        dumpTableRow(result, name, formatterShort.getFormat(date), formatterLong.getFormat(date));
    }

    private void dumpTableRow(StringBuilder result, String... cols) {
        result.append("<tr>");
        for (String col : cols) {
            result.append("<td>").append(col).append("</td>");
        }
        result.append("</tr>\n");
    }

    private void showValues() {
        setLanguage(prefListLocale.getValue());
        setPrefSummary(prefListDay);
        setPrefSummary(prefListDate);
        setPrefSummary(prefListTime);
        setPrefSummary(prefListMessage);
        prefMessagePrefix.setSummary(SettingsImpl.getMessagePrefix(this, prefsInstance));

        updateSummary(null, null);
    }

    // This is used to show the status of some preference in the description
    private void updateSummary(Preference preference, String newValue) {
        // setLanguage(languageKey);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setValue(newValue);
            setPrefSummary(listPreference, newValue);
        }
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary(newValue);
        }

        int messageMode = getInt(prefListMessage, CalendarFormatter.STYLE.LONG);
        boolean addDetails = messageMode == CalendarFormatter.STYLE.FULL || messageMode == CalendarFormatter.STYLE.LONG;
        boolean showMessagePrefix = messageMode == CalendarFormatter.STYLE.FULL || messageMode == CalendarFormatter.STYLE.MEDIUM;
        String messagePrefix = showMessagePrefix ? SettingsImpl.getMessagePrefix(this, this.prefsInstance) : null;

        CalendarFormatter formatter = new CalendarFormatter(
                Locale.getDefault(),
                getInt(prefListDay, CalendarFormatter.STYLE.SHORT),
                getInt(prefListDate, CalendarFormatter.STYLE.SHORT),
                getInt(prefListTime, CalendarFormatter.STYLE.SHORT),
                messagePrefix, addDetails);

        prefExample.setSummary(formatter.add(new StringBuilder(), new Date(), null, null));

        prefLastExample.setSummary(formatter.toString(SettingsImpl.getLast(this)));

        if (Global.debugEnabled) {
            Log.i(Global.LOG_CONTEXT, "updateSummary " + Locale.getDefault() +
                    ", day=" + getInt(prefListDay, CalendarFormatter.STYLE.SHORT) +
                    ", date=" + getInt(prefListDate, CalendarFormatter.STYLE.SHORT) +
                    ", time=" + getInt(prefListTime, CalendarFormatter.STYLE.SHORT) +
                    ", message=" + messageMode +
                    "");
        }
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
        setPrefSummary(prefListLocale, languageKey);
    }

    private void setPrefSummary(ListPreference listPreference) {
        setPrefSummary(listPreference, listPreference.getValue());
    }

    private void setPrefSummary(ListPreference listPreference, String value) {
        int index = listPreference.findIndexOfValue(value);
        CharSequence summary = "";

        if (index >= 0) {
            CharSequence[] names = listPreference.getEntries();
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
