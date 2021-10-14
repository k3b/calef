/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of ical2txt https://github.com/k3b/ical2txt/
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
package de.k3b.ical2txt;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressWarnings("ConstantConditions")
public class CalendarPrinter {
    public static final String LOG_TAG = "k3b-CalendarPrinter";
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
    private final DateFormat formatDateTime;
    private int id = nextId++;

    public CalendarPrinter() {
        this(SHORT, SHORT, MEDIUM, Locale.getDefault());
    }

    public CalendarPrinter(int dateStyle, int timeStyle, int dayStyle, Locale loc) {
        this(getDateFormat(nextId + 1, dateStyle, timeStyle, dayStyle, loc));
    }

    public CalendarPrinter(DateFormat formatDateTime) {
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

    public String toString(Calendar calendar) {
        StringBuilder result = new StringBuilder();
        ComponentList<VEvent> events = calendar.getComponents(VEvent.VEVENT);
        if (events != null) {
            for (VEvent event : events) {
                add(result, event);
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
        return (value != null) ? value.getValue() : null;
    }

    private String getString(Summary value) {
        return (value != null) ? value.getValue() : null;
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
