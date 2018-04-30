package ru.mitina.vaadin.app;

public class Weather {

    private String tDay;

    private String tNigth;

    private String windSpeed;

    private String pressure;

    private String humidity;

    private String icon;

    public String gettDay() {
        double d = Double.parseDouble(tDay);
        String day = String.format("%.0f", d);
        return day;
    }

    public void settDay(String tDay) {
        this.tDay = tDay;
    }

    public String gettNigth() {
        double d = Double.parseDouble(tNigth);
        String n = String.format("%.0f", d);
        return n;
    }

    public void settNigth(String tNigth) {
        this.tNigth = tNigth;
    }

    public String getWindSpeed() {
        double d = Double.parseDouble(windSpeed);
        String w = String.format("%.0f", d);
        return w;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getPressure() {
        double d = Double.parseDouble(pressure);
        double res = d/1.333224;
        String w = String.format("%.0f", res);
        return w;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

