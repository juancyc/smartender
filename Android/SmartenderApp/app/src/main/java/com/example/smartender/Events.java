package com.example.smartender;

import android.content.Context;

import java.util.Objects;

public class Events {

    private Integer id;
    private String date;        //Es conveniente cambiar por un objeto date
    private String hour;        //Es conveniente cambiar por un objeto date
    private String description;
    private String weatherdescription;

    public Events(Integer id, String date, String hour, String description,String weatherdescription){
        this.id = id;
        this.date = date;
        this.hour = hour;
        this.description = description;
        this.weatherdescription = weatherdescription;
    }

    public Events(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeatherdescription() {
        return weatherdescription;
    }

    public void setWeatherdescription(String weatherdescription) {
        this.weatherdescription = weatherdescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Events events = (Events) o;
        return Objects.equals(id, events.id) &&
                Objects.equals(date, events.date) &&
                Objects.equals(hour, events.hour) &&
                Objects.equals(description, events.description) &&
                Objects.equals(weatherdescription, events.weatherdescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, hour, description, weatherdescription);
    }

    @Override
    public String toString() {
        return  date  + " - " +
                hour  + " - " +
                description + " - " + weatherdescription
                ;
    }
}
