/*
 * Copyright (c) 2021-2022 by k3b.
 *
 * This file is part of CalEF (Calendar Entry Formatter) https://github.com/k3b/calef/
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
import android.widget.Toast;

import androidx.annotation.Nullable;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import de.k3b.android.Global;
import de.k3b.android.widget.LocalizedActivity;
import de.k3b.calef.CalendarFormatter;

/**
 * Translates an ICS/VCS stream to a human readable Text.
 */
public class CalefIcsVcs2TxtActivity extends LocalizedActivity {
    public static final String TAG = "k3b.calef";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final int REQUEST_ID_SHARE_RESULT = 1;

    public static void close(Closeable stream, Object source) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.warn("Error close " + source, e);
            }
        }
    }

    public static void sendResult(Activity context, Calendar calendar) {
        CalendarFormatter formatter = SettingsImpl.init(context);
        String textLang = formatter.toString(calendar);

        if (Global.debugEnabled) {
            logger.info("Result {}", textLang);
        }

        Intent startIntent = new Intent(Intent.ACTION_SEND);
        startIntent.setType("text/plain");

        // email subject only first line
        String textKurz = textLang.split("\n\r")[0];
        startIntent.putExtra(Intent.EXTRA_SUBJECT, textKurz);
        startIntent.putExtra(Intent.EXTRA_TEXT, textLang);
        startIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Toast.makeText(context, textLang, Toast.LENGTH_SHORT).show();

        context.startActivityForResult(Intent.createChooser(startIntent, context.getString(R.string.share_using)), REQUEST_ID_SHARE_RESULT);
    }

    public static void toast(Activity context, String message) {
        Toast.makeText(context,
                context.getString(R.string.send_result_toast_message, message),
                Toast.LENGTH_LONG).show();
    }

    static {
        CalendarFormatter.init();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LocalizedActivity.fixLocale(this);    // Support to change locale at runtime
        super.onCreate(savedInstanceState);
        Intent intentIn = getIntent();
        if (intentIn != null) {
            Uri uri = intentIn.getParcelableExtra(Intent.EXTRA_STREAM); // used by send
            if (uri == null) {
                uri = intentIn.getData(); // used by sendTo and view
            }

            if (uri != null) {
                if (Global.debugEnabled) {
                    logger.info("onCreate {}", intentIn.toUri(Intent.URI_INTENT_SCHEME));
                }

                InputStream in = null;
                try {
                    in = getContentResolver().openInputStream(uri);
                    CalendarBuilder builder = new CalendarBuilder();
                    Calendar calendar = builder.build(in);
                    sendResult(this, calendar);
                    SettingsImpl.putLast(this, calendar);
                } catch (Exception ex) {
                    logger.warn("Error in onCreate " + uri, ex);
                    toast(this, getString(R.string.error_cannot_convert_or_resend, uri, ex.getMessage()));
                    setResult(RESULT_CANCELED);
                    finish();
                } finally {
                    close(in, uri);
                }
            }
        }
    }

    // support for onActivityResult
    // CalendarApp -> CalefIcsVcs2TxtActivity -> result-send-Activity -> CalefIcsVcs2TxtActivity -> return-result-to-CalendarApp
    // Forward result of result-send-Activity
    // to caller of this activity {CalendarApp}.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_SHARE_RESULT) {
            setResult(resultCode, data);
            String message = "" + resultCode;
            if (data != null) {
                message += data.getData();
            }
            toast(this, message);
            finish();
        }

    }
}
