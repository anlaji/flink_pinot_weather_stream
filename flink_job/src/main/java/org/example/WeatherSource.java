package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherSource implements SourceFunction<String> {

    private volatile boolean running = true;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        while (running) {
            // Each 10 secs it calls the api and emit a string json
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://api.open-meteo.com/v1/forecast" +
                                    "?latitude=40.4&longitude=-3.7&current_weather=true"
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode current = root.get("current_weather");

            if (current != null) {
                // It emits an event
                // Its not stored in memory
                // It does not collect it, it just sends to the next operator
                ctx.collect(current.toString());
            }

            Thread.sleep(10_000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
