package ru.mitina.vaadin.app;

public class Weather {

    private String date;

    private String tDay;

    private double tNigth;

    private double windSpeed;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String gettDay() {
        double d = Double.parseDouble(tDay) - 273;
        String day = String.format("%.2f", d);
        return day;
    }

    public void settDay(String tDay) {
        this.tDay = tDay;
    }

    public double gettNigth() {
        this.tNigth = tNigth - 273;
        String formattedDouble = String.format("%.2f", this.tNigth);
        this.tNigth = Double.parseDouble(formattedDouble);
        return this.tNigth;
    }

    public void settNigth(double tNigth) {
        this.tNigth = tNigth;
    }

    public double getWindSpeed() {
        String formattedDouble = String.format("%.2f", this.windSpeed);
        this.windSpeed = Double.parseDouble(formattedDouble);
        return this.windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
