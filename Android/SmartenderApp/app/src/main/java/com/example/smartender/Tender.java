package com.example.smartender;

import java.util.Objects;

public class Tender {

    private int id;
    private String date;
    private String hour;
    private String reason;

    public Tender(int id, String date, String hour, String reason) {
        this.id = id;
        this.date = date;
        this.hour = hour;
        this.reason = reason;
    }

    public Tender(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tender tender = (Tender) o;
        return id == tender.id &&
                Objects.equals(date, tender.date) &&
                Objects.equals(hour, tender.hour) &&
                Objects.equals(reason, tender.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, hour, reason);
    }

    @Override
    public String toString() {
        return  date+"-"+hour+"-"+reason ;
    }
}
