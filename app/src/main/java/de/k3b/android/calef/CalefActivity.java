/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of calef (calendar entry formatter) https://github.com/k3b/calef/
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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import de.k3b.calef.CalendarFormatter;

/**
 * Translates an ICS/VCS stream to a human readable Text.
 */
public class CalefActivity extends Activity {
    private static final String TAG = "k3b.calef";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final CalendarFormatter CALENDAR_FORMATTER = new CalendarFormatter();

    public static void close(Closeable stream, Object source) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.warn("Error close " + source, e);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri;
        if (intent != null && null != (uri = intent.getParcelableExtra(Intent.EXTRA_STREAM))) {
            logger.info("onCreate {}", intent.toUri(Intent.URI_INTENT_SCHEME));

            InputStream in = null;
            try {
                in = getContentResolver().openInputStream(uri);
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(in);
                String text = CALENDAR_FORMATTER.toString(calendar);

                // TODO
            } catch (Exception ex) {
                logger.warn("Error in onCreate " + uri, ex);
            } finally {
                close(in, uri);
            }
        }
    }
}
