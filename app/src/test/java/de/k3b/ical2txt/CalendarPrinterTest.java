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

import static org.junit.Assert.assertEquals;
import static de.k3b.ical2txt.CalendarPrinter.SHORT;

import org.junit.Test;

import java.util.Date;
import java.util.Locale;

public class CalendarPrinterTest {
    private static final Date DATE_TIME_20211224 = new Date(121, 12 - 1, 24, 12, 34);

    @Test
    public void add_germanyShort_ok() {
        CalendarPrinter.debugEnabled = true;
        CalendarPrinter calendarPrinter = new CalendarPrinter(SHORT, SHORT, SHORT, Locale.GERMANY);

        StringBuilder result = new StringBuilder();
        calendarPrinter.add(result, DATE_TIME_20211224,
                "summary", "description");
        // android "📅Fr. 24.12.21 12:34..."
        // linux "📅Fr 24.12.21 12:34..."
        String actual = result.toString().replace("Fr.", "Fr");
        assertEquals("📅Fr 24.12.21 12:34 summary\n" +
                "description", actual);
    }

    @Test
    public void add_usShort_ok() {
        CalendarPrinter.debugEnabled = true;
        CalendarPrinter calendarPrinter = new CalendarPrinter(SHORT, SHORT, SHORT, Locale.US);

        StringBuilder result = new StringBuilder();
        calendarPrinter.add(result, DATE_TIME_20211224,
                "summary", "description");
        String actual = result.toString();
        assertEquals("📅Fri 12/24/21 12:34 PM summary\n" +
                "description", actual);
    }
}