# mixpanel-export

Export Mixpanel events from Java

###Build the jar with Maven
   - `git clone` repo and `cd` to the project root
   - `mvn install`  
   _In typical Maven fashion, the jar is output to the `target` folder._

###Example

See [ExamplesTest.java](https://github.com/cdimascio/mixpanel-export/blob/master/test/java/com/cmd/mixpanel/test/ExamplesTest.java)

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

