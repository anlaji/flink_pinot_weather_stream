package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

public class PinotRestSink implements SinkFunction<WeatherEvent> {

    private final String pinotUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public PinotRestSink(String pinotUrl) {
        this.pinotUrl = pinotUrl;
    }

    @Override
    public void invoke(WeatherEvent value, Context context) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(pinotUrl).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String payload = mapper.writeValueAsString(Collections.singletonList(value));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200 && responseCode != 202) {
            System.err.println("Failed to send event to Pinot: " + responseCode);
        }

        conn.disconnect();
    }
}
