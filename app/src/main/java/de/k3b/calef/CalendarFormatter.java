/*
 * Copyright (c) 2021 by k3b.
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
package de.k3b.calef;

import static net.fortuna.ical4j.util.CompatibilityHints.KEY_NOTES_COMPATIBILITY;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_PARSING;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_UNFOLDING;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_VALIDATION;
import static net.fortuna.ical4j.util.CompatibilityHints.setHintEnabled;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.util.MapTimeZoneCache;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


@SuppressWarnings("ConstantConditions")
public class CalendarFormatter {
    public static final String LOG_TAG = "k3b-CalendarForm";
    /**
     * OFF this part is suppressed
     */
    public static final int OFF = -1;
    /**
     * SHORT is completely numeric, such as 12.13.52 or 3:30pm
     */
    public static final int SHORT = java.text.DateFormat.SHORT;
    /**
     * MEDIUM is longer, such as Jan 12, 1952
     */
    public static final int MEDIUM = java.text.DateFormat.MEDIUM;
    /**
     * LONG is longer, such as January 12, 1952 or 3:30:32pm
     */
    public static final int LONG = java.text.DateFormat.LONG;
    /**
     * FULL is pretty completely specified, such as Tuesday, April 12, 1952 AD or 3:30:42pm PST.
     */
    public static final int FULL = java.text.DateFormat.FULL;
    private static final Logger LOGGER = LoggerFactory.getLogger(LOG_TAG);
    public static boolean debugEnabled = false;
    private static int nextId = 1;
    private static final Pattern quotedPritable = Pattern.compile(".*=[0-9a-fA-F]{2}.*");
    private static final QuotedPrintableCodec decoder = new QuotedPrintableCodec();

    static {
        // prevent online download of timezones to translate events from utc to local time
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());

        // CompatibilityHints.setHintEnabled(setHintEnabled.KEY_XXXX,true) : Make as much compatible as possible
        setHintEnabled(KEY_RELAXED_UNFOLDING, true);
        setHintEnabled(KEY_RELAXED_PARSING, true);
        setHintEnabled(KEY_RELAXED_VALIDATION, true);
        setHintEnabled(KEY_OUTLOOK_COMPATIBILITY, true);
        setHintEnabled(KEY_NOTES_COMPATIBILITY, true);
    }

    private final DateFormat formatDateTime;
    private int id = nextId++;

    public CalendarFormatter() {
        this(SHORT, SHORT, MEDIUM, Locale.getDefault());
    }

    public CalendarFormatter(int dateStyle, int timeStyle, int dayStyle, Locale loc) {
        this(getDateFormat(nextId + 1, dateStyle, timeStyle, dayStyle, loc));
    }

    public CalendarFormatter(DateFormat formatDateTime) {
        this.id++;
        this.formatDateTime = formatDateTime;
    }

    @SuppressWarnings("ConstantConditions")
    private static DateFormat getDateFormat(int id, int dateStyle, int timeStyle, int dayStyle, Locale loc) {
        DateFormat format = null; // new SimpleDateFormat("'📅'EE dd.MM HH:mm z 'kw' w");
        // https://howtodoinjava.com/java/date-time/java-date-formatting/
        // EE=Day name i.e. mo=monday
        // z=timezone (ie pst or MEZ)
        // w=week-of-year

        if (dateStyle != OFF && timeStyle != OFF) {
            format = DateFormat.getDateTimeInstance(dateStyle, timeStyle, loc);
        } else //noinspection ConstantConditions
            if (dateStyle != OFF && timeStyle == OFF) {
                format = DateFormat.getDateInstance(dateStyle, loc);
            } else if (dateStyle == OFF && timeStyle != OFF) {
                format = DateFormat.getTimeInstance(timeStyle, loc);
            } else if (dayStyle == OFF) {
                throw new IllegalArgumentException("Illegal date style " + dateStyle + ", time style " + timeStyle);
            }

        if (format != null && !(format instanceof SimpleDateFormat)) {
            LOGGER.warn("getDateFormat#{}: Locale {} not supported using {}", id, loc, format);
            return format;
        }

        StringBuilder pattern = new StringBuilder();
        pattern.append("'📅'");

        // "EEE" is day of week (i.e. monday)
        if (dayStyle == SHORT) {
            pattern.append("E ");
        } else if (dayStyle == MEDIUM) {
            pattern.append("EE ");
        } else if (dayStyle == LONG || dayStyle == FULL) {
            pattern.append("EEE ");
        }

        if (format != null) {
            pattern.append(((SimpleDateFormat) format).toPattern().replace(",", ""));
        }

        if (debugEnabled) {
            LOGGER.info("getDateFormat#{}(format={}, locale={})", id, pattern, loc);
        }
        return new SimpleDateFormat(pattern.toString(), loc);
    }

    protected static String decode(String val) {
        if (val != null && quotedPritable.matcher(val).matches()) {
            // microsoft vsc files quotedPritable is not handled by ical4j
            try {
                return decoder.decode(val);
            } catch (DecoderException e) {
            }
        }
        return val;
    }

    public String toString(Calendar calendar) {
        StringBuilder result = new StringBuilder();
        ComponentList events = calendar.getComponents(VEvent.VEVENT);
        if (events != null) {
            for (Object event : events) {
                add(result, (VEvent) event);
            }
        }
        return result.toString();
    }

    private void add(StringBuilder result, VEvent event) {
        if (event != null) {
            add(result, getDate(event.getStartDate()), getString(event.getSummary()), getString(event.getDescription()));
        }
    }

    private String getString(Description value) {
        return decode((value != null) ? value.getValue() : null);
    }

    private String getString(Summary value) {
        return decode((value != null) ? value.getValue() : null);
    }

    private Date getDate(DtStart startDate) {
        net.fortuna.ical4j.model.Date date;
        if (startDate != null && null != (date = startDate.getDate()) && date.getTime() != 0) {
            return new Date(date.getTime());
        }
        return null;
    }

    public void add(StringBuilder result, Date dateTime, String summary, String description) {
        if (dateTime != null) {
            result.append(formatDateTime.format(dateTime)).append(" ");
        }
        if (summary != null) {
            result.append(summary);
        }

        if (description != null) {
            result.append("\n").append(description);
        }
        if (debugEnabled) {
            LOGGER.info("add#{} => {}", id, result);
        }
    }
}
