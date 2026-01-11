package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WeatherStreamingJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        // Data Ingestion
        DataStream<String> rawStream =
                env.addSource(new WeatherSource());

        ObjectMapper mapper = new ObjectMapper();

        DataStream<WeatherEvent> weatherStream = rawStream.map(new MapFunction<String, WeatherEvent>() {
            @Override
            public WeatherEvent map(String value) throws Exception {
                JsonNode node = mapper.readTree(value);
                double temperature = node.get("temperature").asDouble();
                double windspeed = node.get("windspeed").asDouble();
                String city = "Madrid"; // TODO, check different lat/lng for the city and pass it over the ctx
                long eventTime = System.currentTimeMillis(); // current timestamp

                return new WeatherEvent(city, temperature, windspeed, eventTime);
            }
        });


        String pinotIngestUrl = "http://localhost:9000/ingestEvents?tableName=weather";
        weatherStream.addSink(new PinotRestSink(pinotIngestUrl));

        // De momento solo imprimimos
        weatherStream.print();

        // Execute validates the above graph and translate it in an execution plan.
        // Job is alive
        env.execute("Weather Streaming Job");
    }
}
