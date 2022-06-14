package org.nexuse2e.util;

import org.junit.Test;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ServerPropertiesUtilTest {

    @Test
    public void replaceServerPropertiesROOTDIR() {
        String testdata = "abc ${nexus.server.root} def${nexus.server.root}/ghi";
        String result = ServerPropertiesUtil.replaceServerProperties(testdata);
        assertEquals("Not replaced in unittest because of bean context (Engine is null)","abc ${nexus.server.root} def${nexus.server.root}/ghi", result);
    }
    @Test
    public void replaceServerPropertiesSERVER_CURRENTMILLIS() {
        String testdata = "abc ${nexus.server.time.millis} def${nexus.server.time.millis}/ghi";
        String result = ServerPropertiesUtil.replaceServerProperties(testdata);
        assertTrue(result.matches("abc [0-9]+ def[0-9]+/ghi"));
    }
    @Test
    public void replaceServerPropertiesSERVER_CURRENTTIME() {
        String testdata = "abc ${nexus.server.time.formated} def${nexus.server.time.formatted}/ghi";
        String result = ServerPropertiesUtil.replaceServerProperties(testdata);
        assertTrue(result.matches("abc \\d+ def[0-9]+/ghi"));
    }
    @Test
    public void replaceServerPropertiesRegex() {
        String testdata = "abc ${nexus.server.time.formated} def${nexus.server.time.formatted}/ghi";
        Matcher m = Pattern.compile("\\$\\{([^}]+)}").matcher(testdata);
        m.find();
        assertEquals("nexus.server.time.formated",m.group(1));
        m.find();
        assertEquals("nexus.server.time.formatted",m.group(1));
    }

    @Test
    public void replaceServerPropertiesDatePattern() {
        String testdata = "abc ${nexus.server.time.formatted.YYYY} def${nexus.server.time.formatted.ABCD}/ghi";
        String result = ServerPropertiesUtil.replaceServerProperties(testdata);
        SimpleDateFormat sd = new SimpleDateFormat("YYYY");
        assertEquals("abc "+sd.format(new Date())+" def${nexus.server.time.formatted.ABCD}/ghi",result);
    }

    @Test
    public void replaceServerPropertiesDatePattern2() {
        String testdata = "ddd${nexus.server.time.formated}abc ${nexus.server.time.formatted.yyyy-MM-dd'T'HH:mm:ssZ} def${nexus.server.time.formatted.yyyy-MM-dd'T'HH:mm:ssZ}/ghi";
//        String testdata = "${nexus.server.time.formatted.yyyy-MM-dd'T'HH:mm:ssZ}";
        Date now = new Date();
        String result = ServerPropertiesUtil.replaceServerProperties(testdata);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        assertTrue(result.matches("ddd[0-9]+abc \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{4} def\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{4}/ghi"));
    }

}