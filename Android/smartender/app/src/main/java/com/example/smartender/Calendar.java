package com.example.smartender;

import java.util.Objects;

public class Calendar {

    private Integer id;
    private String date;
    private String hour;
    private String description;

    public Calendar(Integer id,String date,String hour,String description){
        this.id = id;
        this.date = date;
        this.hour = hour;
        this.description = description;
    }

    public  Calendar(){

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calendar calendar = (Calendar) o;
        return Objects.equals(id, calendar.id) &&
                Objects.equals(date, calendar.date) &&
                Objects.equals(hour, calendar.hour) &&
                Objects.equals(description, calendar.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, hour, description);
    }

    @Override
    public String toString() {
        return  date  + " - " +
                hour  + " - " +
                description;
    }
}
