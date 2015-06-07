package com.cmd.mixpanel.test;

import com.cmd.mixpanel.MixpanelException;
import org.json.JSONObject;
import org.junit.Test;
import com.cmd.mixpanel.Mixpanel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExamplesTest {

    private static final String MIXPANEL_API_KEY = "<your-key>";
    private static final String MIXPANEL_API_SECRET = "<your-secret>";

    @Test
    public void example() throws MixpanelException {
        LocalDate from = LocalDate.parse("20150604", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate to = LocalDate.parse("20150604", DateTimeFormatter.BASIC_ISO_DATE);

        List<JSONObject> res = Mixpanel.getInstance(MIXPANEL_API_KEY, MIXPANEL_API_SECRET).
                setTimeouts(10000, 90000).
                export(from, to);

        System.out.println(res);
    }
}
