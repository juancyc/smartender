package com.example.smartender;

public class Weather {
    private String temperature;
    private String humidity;
    private String description;
    private String city;
    private String icon;

    public Weather(){
        temperature = "--";
        humidity = "--";
        description= "";
        city = "Ubicacion desconocida";
        icon = "";
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
