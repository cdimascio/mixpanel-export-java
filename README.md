# mixpanel-export

Export Mixpanel events from Java

###Usage

See test/java/com.cmd/mixpanel/test/ExamplesTest

```java

public class Example {
    private static final String MIXPANEL_API_KEY = "<your-key>";
    private static final String MIXPANEL_API_SECRET = "<your-secret>";

    public static void main(String[] args) {
        LocalDate from = LocalDate.parse("20150604", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate to = LocalDate.parse("20150604", DateTimeFormatter.BASIC_ISO_DATE);

        List<JSONObject> res = Mixpanel.getInstance(MIXPANEL_API_KEY, MIXPANEL_API_SECRET).
                export(from, to);
    }
}    
```

