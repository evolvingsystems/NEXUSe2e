package org.nexuse2e.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author jjerke
 *
 */
@SuppressWarnings("unused")
public class DateTimeTest {

    private DateTime dt = new DateTime();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void constructFromShortArray() {
        short[] parameters = {19, 70, 1, 1, 1, 0, 3, 0};
        DateTime dt = new DateTime(parameters);

        assertEquals(19, dt.getCentury());
        assertEquals(70, dt.getYear());
        assertEquals(1, dt.getMonth());
        assertEquals(1, dt.getDay());
        assertEquals(1, dt.getHour());
        assertEquals(0, dt.getMinute());
        assertEquals(3, dt.getSeconds());
        assertEquals(0, dt.getMilli());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructFromShortArrayWithInvalidLength() {
        short[] parameters = {19, 70, 1, 1};
        DateTime dt = new DateTime(parameters);
    }

    @Test
    @Ignore
    public void constructFromLong() {

        int offset = TimeZone.getDefault().getRawOffset();

        DateTime dt = new DateTime(3600L);
        Date reference = new Date(3600L);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(reference);

        assertEquals(19, dt.getCentury());
        assertEquals(70, dt.getYear());
        assertEquals(1, dt.getMonth());
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), dt.getDay());
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), dt.getHour());
        assertEquals(calendar.get(Calendar.MINUTE), dt.getMinute());
        assertEquals(calendar.get(Calendar.SECOND), dt.getSeconds());
        assertEquals(calendar.get(Calendar.MILLISECOND), dt.getMilli());
    }

    @Test
    public void constructFromString() throws ParseException {
        DateTime dt = new DateTime("1970-01-01T01:00:03.600+01:00");

        assertEquals(19, dt.getCentury());
        assertEquals(70, dt.getYear());
        assertEquals(1, dt.getMonth());
        assertEquals(1, dt.getDay());
        assertEquals(1, dt.getHour());
        assertEquals(0, dt.getMinute());
        assertEquals(3, dt.getSeconds());
        assertEquals(600, dt.getMilli());
    }

    @Test(expected = ParseException.class)
    public void constructFromStringWithInvalidString() throws ParseException {
        DateTime dt = new DateTime("Invalid DateTime string!");
    }

    @Test
    @Ignore
    public void testValues() {
        Instant reference =
                ZonedDateTime.of(LocalDateTime.of(1970, 1, 1, 1, 0, 3, 0), TimeZone.getDefault().toZoneId())
                .toInstant();

        short[] parameters = {19, 70, 1, 1, 1, 0, 3, 0};
        DateTime dt = new DateTime(parameters);

        short[] returned = dt.getValues();
        assertEquals(19, returned[0]);
        assertEquals(70, returned[1]);
        assertEquals(1, returned[2]);
        assertEquals(1, returned[3]);
        assertEquals(1, returned[4]);
        assertEquals(0, returned[5]);
        assertEquals(3, returned[6]);
        assertEquals(0, returned[7]);

        Date returnedDate = dt.toDate();
        assertEquals(reference.toEpochMilli(), returnedDate.getTime());

        assertEquals(reference.toEpochMilli(), dt.toLong());

        assertTrue("1970-01-01T01:00:03".equalsIgnoreCase(dt.toString()));
    }

    @Test
    public void testParses() throws ParseException {
        DateTime dtOne = DateTime.parse("1970-01-01T01:00:03.600+01:00");
        assertEquals(19, dtOne.getCentury());
        assertEquals(70, dtOne.getYear());
        assertEquals(1, dtOne.getMonth());
        assertEquals(1, dtOne.getDay());
        assertEquals(1, dtOne.getHour());
        assertEquals(0, dtOne.getMinute());
        assertEquals(3, dtOne.getSeconds());
        assertEquals(600, dtOne.getMilli());

        DateTime dtTwo = DateTime.parse("1970-01-01T01:00:03.600+01:00");
        assertEquals(19, dtTwo.getCentury());
        assertEquals(70, dtTwo.getYear());
        assertEquals(1, dtTwo.getMonth());
        assertEquals(1, dtTwo.getDay());
        assertEquals(1, dtTwo.getHour());
        assertEquals(0, dtTwo.getMinute());
        assertEquals(3, dtTwo.getSeconds());
        assertEquals(600, dtTwo.getMilli());
    }
}
