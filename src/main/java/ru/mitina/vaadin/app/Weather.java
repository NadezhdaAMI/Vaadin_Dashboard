package ru.mitina.vaadin.app;

public class Weather {

    private String tDay;

    private String tNigth;

    private String windSpeed;

    public String gettDay() {
        double d = Double.parseDouble(tDay) - 273;
        String day = String.format("%.2f", d);
        return day;
    }

    public void settDay(String tDay) {
        this.tDay = tDay;
    }

    public String gettNigth() {
        double d = Double.parseDouble(tNigth) - 273;
        String n = String.format("%.2f", d);
        return n;
    }

    public void settNigth(String tNigth) {
        this.tNigth = tNigth;
    }

    public String getWindSpeed() {
        double d = Double.parseDouble(windSpeed);
        String w = String.format("%.2f", d);
        return w;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
