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
import java.util.TimeZone;
import java.util.regex.Pattern;


@SuppressWarnings("ConstantConditions")
public class CalendarFormatter {
    public static final String LOG_TAG = "k3b-CalEF";
    private static int nextDebugId = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(LOG_TAG);
    public static boolean debugEnabled = false;
    /**
     * SimpleDateFormat that is used if hh:mm is not 00:00
     */
    private final DateFormat formatDateTime;
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

    /**
     * SimpleDateFormat that is used if hh:mm is 00:00
     */
    private final DateFormat formatDateWithoutTime;
    /**
     * used for debugging to differentiate which CalendarFormatter instance is used
     */
    private int debugId = nextDebugId++;

    public CalendarFormatter() {
        this(Locale.getDefault(), STYLE.MEDIUM, STYLE.SHORT, STYLE.SHORT);
    }

    public CalendarFormatter(Locale loc, int dayStyle, int dateStyle, int timeStyle) {
        this(getDateFormat(nextDebugId + 1, loc, dayStyle, dateStyle, timeStyle),
                getDateFormat(nextDebugId + 1, loc, dayStyle, dateStyle, STYLE.OFF));
    }

    public CalendarFormatter(DateFormat formatDateTime, DateFormat formatDateWithoutTime) {
        this.debugId++;
        this.formatDateTime = formatDateTime;
        this.formatDateWithoutTime = formatDateWithoutTime;
    }

    @SuppressWarnings("ConstantConditions")
    private static DateFormat getDateFormat(int debugId, Locale loc, int dayStyle, int dateStyle, int timeStyle) {
        DateFormat format = null; // new SimpleDateFormat("'📅'EE dd.MM HH:mm z 'kw' w");
        // https://howtodoinjava.com/java/date-time/java-date-formatting/
        // EE=Day name i.e. mo=monday
        // z=timezone (ie pst or MEZ)
        // w=week-of-year

        if (dateStyle != STYLE.OFF && timeStyle != STYLE.OFF) {
            format = DateFormat.getDateTimeInstance(dateStyle, timeStyle, loc);
        } else //noinspection ConstantConditions
            if (dateStyle != STYLE.OFF && timeStyle == STYLE.OFF) {
                format = DateFormat.getDateInstance(dateStyle, loc);
            } else if (dateStyle == STYLE.OFF && timeStyle != STYLE.OFF) {
                format = DateFormat.getTimeInstance(timeStyle, loc);
            } else if (dayStyle == STYLE.OFF) {
                throw new IllegalArgumentException("Illegal date style " + dateStyle + ", time style " + timeStyle);
            }

        if (format != null && !(format instanceof SimpleDateFormat)) {
            LOGGER.warn("getDateFormat#{}: Locale {} not supported using {}", debugId, loc, format);
            return format;
        }

        StringBuilder pattern = new StringBuilder();
        pattern.append("'📅'");

        // "EEE" is day of week (i.e. monday)
        if (dayStyle == STYLE.SHORT) {
            pattern.append("EE ");
        } else if (dayStyle == STYLE.MEDIUM) {
            pattern.append("EEE ");
        } else if (dayStyle == STYLE.LONG || dayStyle == STYLE.FULL) {
            pattern.append("EEEE ");
        }

        TimeZone timeZone = TimeZone.getDefault();
        if (format != null) {
            pattern.append(((SimpleDateFormat) format).toPattern().replace(",", ""));
            timeZone = format.getTimeZone();
        }

        if (debugEnabled) {
            LOGGER.info("getDateFormat#{}(format={}, locale={})", debugId, pattern, loc);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern.toString(), loc);
        dateFormat.setTimeZone(timeZone);
        return dateFormat;
    }

    /** executes un-escaping. I.E "=0D=0A" is translated back to "\r\n" */
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

    public StringBuilder add(StringBuilder result, Date dateTime, String summary, String description) {
        if (dateTime != null) {
            result.append(getFormat(dateTime)).append(" ");
        }
        if (summary != null) {
            result.append(summary);
        }

        if (description != null) {
            result.append("\n").append(description);
        }
        if (debugEnabled) {
            LOGGER.info("add#{} => {}", debugId, result);
        }
        return result;
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

    private String getFormat(Date dateTime) {
        if (dateTime.getHours() == 0 && dateTime.getMinutes() == 0) {
            // suppress hours/minutes if 00:00 for localdate
            return formatDateWithoutTime.format(dateTime);
        }
        // todo suppress hours/minutes if localdate converted to utc is 00:00
        // if dateTime.getTimezoneOffset() == mhour/minute
        return formatDateTime.format(dateTime);
    }

    public static class STYLE {
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

    }
}
