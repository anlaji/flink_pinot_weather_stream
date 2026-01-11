package org.example;

public class WeatherEvent {

    public String city;
    public double temperature;
    public double windspeed;
    public long eventTime;

    // Constructor
    public WeatherEvent() {
    }

    public WeatherEvent(String city, double temperature, double windspeed, long eventTime) {
        this.city = city;
        this.temperature = temperature;
        this.windspeed = windspeed;
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "WeatherEvent{" +
                "city='" + city + '\'' +
                ", temperature=" + temperature +
                ", windspeed=" + windspeed +
                ", eventTime=" + eventTime +
                '}';
    }
}