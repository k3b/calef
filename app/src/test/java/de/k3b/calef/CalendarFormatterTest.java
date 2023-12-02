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

import static org.junit.Assert.assertEquals;
import static de.k3b.calef.CalendarFormatter.STYLE;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;
import java.util.Date;
import java.util.Locale;

public class CalendarFormatterTest {
    private static final Date DATE_TIME_20211224 = new Date(121, 12 - 1, 24, 12, 34);
    private static final Date DATE_TIME_20211224_WITHOUT_TIME = new Date(121, 12 - 1, 24);

    @BeforeClass
    public static void init() {
        // fix failing on buildserver (from https://stackoverflow.com/questions/6981890/new-datelong-gives-different-results )
        TimeZone.setDefault(TimeZone.getTimeZone("CET")); // works inside android-studio
        System.setProperty("user.timezone", "CET"); // works on commandline runner
    }

    @Test
    public void add_germanyShort_ok() {
        CalendarFormatter.debugEnabled = true;
        CalendarFormatter calendarFormatter = createCalendarFormatter(Locale.GERMANY);

        StringBuilder result = new StringBuilder();
        calendarFormatter.add(result, DATE_TIME_20211224,
                "summary", "description");
        assertEquals(fix("📅Fr 24.12.21 12:34 summary\n" +
                "description"), fix(result.toString()));
    }

    @Test
    public void add_usShort_ok() {
        CalendarFormatter.debugEnabled = true;
        CalendarFormatter calendarFormatter = createCalendarFormatter(Locale.US);

        StringBuilder result = new StringBuilder();
        calendarFormatter.add(result, DATE_TIME_20211224,
                "summary", "description");
        assertEquals(fix("📅Fri 12/24/21 12:34 PM summary\n" +
                "description"), fix(result.toString()));
    }

    @Test
    public void add_usShortWithoutTime_ok() {
        CalendarFormatter.debugEnabled = true;
        CalendarFormatter calendarFormatter = createCalendarFormatter(Locale.US);

        StringBuilder result = new StringBuilder();
        calendarFormatter.add(result, DATE_TIME_20211224_WITHOUT_TIME,
                "summary", "description");
        assertEquals(fix("📅Fri 12/24/21 summary\n" +
                "description"), fix(result.toString()));
    }

    @Test
    public void toString_etarIcsWithEmojiesGerman_ok() throws Exception {
        // retrieved from etar calendar app 🚶
        String myVCalendarString = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n" +
                "BEGIN:VEVENT\n" +
                "DTSTAMP:20211010T123846Z\n" +
                "SUMMARY:18:50 Bus → Bahnhof\n" +
                "DTSTART:20211028T164000Z\n" +
                "DESCRIPTION:18:53 Zuhause\\n  🚶 Fußweg 155 m für 2 min\\n18:55 Bus\n" +
                " haltestelle\\n\\n18:55 Bushaltestelle\\n  🚌 123 → Bahnhof\n" +
                " \\n19:07 Bahnhof\n" +
                "DTEND:20211028T165400Z\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR\n";
        CalendarFormatter.debugEnabled = true;

        CalendarFormatter calendarFormatter = createCalendarFormatter(Locale.GERMANY);

        String actual = calendarFormatter.toString(new CalendarBuilder()
                .build(new StringReader(myVCalendarString)));

        assertEquals(fix("📅Do 28.10.21 18:40 18:50 Bus → Bahnhof\n" +
                "18:53 Zuhause\n" +
                " 🚶 Fußweg 155 m für 2 min\n" +
                "18:55 Bushaltestelle\n" +
                "\n" +
                "18:55 Bushaltestelle\n" +
                " 🚌 123 → Bahnhof\n" +
                "19:07 Bahnhof"), fix(actual));
    }

    // fix calendar string generation
    // android "📅Fr. 24.12.21 12:34..."
    // linux "📅Fr 24.12.21 12:34..."
    private String fix(String actual) {
        return actual
                .replace(". ", " ")
                .replaceAll("[\\u00a0\\u202F\\u2060]+"," ") // non-breaking-space aka &nbsp;
//                .replaceAll("\\s+"," ") // other type of blank, tab, cr, lf
                .replace("  ", " ")
                .trim();
    }

    @Test
    public void toString_vcs7BitEncodedGerman_ok() throws Exception {
        // retrieved from etar calendar app 🚶
        String myVCalendarString = "BEGIN:VCALENDAR\n" +
                "VERSION:1.0\n" +
                "PRODID:vCal ID default\n" +
                "BEGIN:VEVENT\n" +
                "UID:42280\n" +
                "DTSTART:20211028T164000Z\n" +
                "SUMMARY;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:Test\n" +
                "DESCRIPTION;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:=F0=9F=9A=8C Hallo Welt=0D=0A(Achtung: Zeiten k=C3=B6nnen gemeldete Versp=C3=A4tungen enthalten)\n" +
                "RRULE:FREQ=WEEKLY;WKST=MO;BYDAY=SU\n" +
                "DURATION:P780S\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR\n";
        CalendarFormatter.debugEnabled = true;

        CalendarFormatter calendarFormatter = createCalendarFormatter(Locale.GERMANY);

        String actual = calendarFormatter.toString(new CalendarBuilder()
                .build(new StringReader(myVCalendarString)));
        assertEquals(fix("📅Do 28.10.21 18:40 Test\n" +
                "🚌 Hallo Welt\r\n" +
                "(Achtung: Zeiten können gemeldete Verspätungen enthalten)"), fix(actual));
    }

    private CalendarFormatter createCalendarFormatter(Locale locale) {
        return new CalendarFormatter(locale, STYLE.SHORT, STYLE.SHORT, STYLE.SHORT, null, true);
    }

    @Test
    public void decode_newline_ok() {
        String actual = CalendarFormatter.decode("hello=0D=0Aworld");
        assertEquals(fix("hello\r\n" +
                "world"), fix(actual));
    }
}