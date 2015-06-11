package com.cmd.mixpanel;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Mixpanel {
    private static final Map<String,Mixpanel> instances = new HashMap<>();
    private final String apiKey;
    private final String apiSecret;
    private final String apiEndpoint = "http://data.mixpanel.com/api/2.0/export";
    private long socketTimeout = 60000;
    private long connectionTimeout = 10000;

    /**
     * Returns the Mixpanel instance for the given api key and secret.
     * @param apiKey The Mixpanel API key
     * @param apiSecret The Mixpanel API Secret
     * @return the Mixpanel instance for the given api key and secret.
     */
    public synchronized static Mixpanel getInstance(String apiKey, String apiSecret) {
        Mixpanel instance = instances.get(apiKey+apiSecret);
        if (instance == null) {
            instance = new Mixpanel(apiKey, apiSecret);
        }
        return instance;
    }

    /**
     * Set the connection and socket timeouts
     * @param connection The timeout until the connection with the server is established (in milliseconds).
     *               Default is 10000. Set to 0 to disable timeout.
     * @param socket The timeout to receive data (in milliseconds).
     *               Default is 60000. Set to 0 to disable timeout.
     */
    public Mixpanel setTimeouts(long connection, long socket) {
        this.connectionTimeout = connection;
        this.socketTimeout = socket;
        return this;
    }

    /**
     * Export data from Mixpanel
     * @param from The date from which to begin querying from. This date is inclusive.
     * @param to The date from which to stop querying from. This date is inclusive.
     * @return The response body as a list of JSONObjects
     * @throws MixpanelException
     */
    public List<JSONObject> export(LocalDate from, LocalDate to) throws MixpanelException {
        try {
            Map<String, Object> params = new TreeMap<String, Object>() {{
                put("api_key", apiKey);
                put("from_date", from.format(DateTimeFormatter.ISO_LOCAL_DATE));
                put("to_date", to.format(DateTimeFormatter.ISO_LOCAL_DATE));
                put("expire", Util.expire().toString());}};

            String sig = Util.sig(apiSecret, params);
            params.put("sig",sig);

            Unirest.setTimeouts(connectionTimeout,socketTimeout);
            HttpResponse<String> res = Unirest.
                    get(apiEndpoint).
                    queryString(params).
                    asString();

            if (res.getStatus() != 200) {
                throw new MixpanelException(res.getStatus()+": "+res.getStatusText());
            }

            return Util.jsonList(res.getBody());
        } catch (UnirestException e) {
            throw new MixpanelException(e);
        }
    }

    private Mixpanel(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        instances.put(apiKey+apiSecret, this);
    }

    private static class Util {
        static String sig(String apiSecret, Map<String,Object> params) {
            return md5(params.entrySet().stream().
                    map(e -> e.getKey()+"="+e.getValue().toString()).
                    sorted().
                    collect(Collectors.joining()) + apiSecret);
        }

        static Long expire() {
            Instant now = Instant.now(Clock.systemUTC());
            now.plusMillis(Duration.ofDays(1).toMillis());
            return now.toEpochMilli();
        }

        static String md5(String s) {
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(s.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte byt : digest) {
                    String hex = Integer.toHexString(0xFF & byt);
                    if (hex.length() == 1) {
                        sb.append('0');
                    }
                    sb.append(hex);
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        static List<JSONObject> jsonList(String s) {
            if (s == null || s.trim().isEmpty()) {
                return Collections.emptyList();
            }
            List<JSONObject> json = new ArrayList<>();
            for (String line : s.split("\n")) {
                json.add(new JSONObject(line));
            }
            return json;
        }
    }
}
