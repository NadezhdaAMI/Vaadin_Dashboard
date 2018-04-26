package ru.mitina.vaadin.app;

public class Weather {

    private String date;

    private double tDay;

    private double tNigth;

    private double windSpeed;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double gettDay() {
        this.tDay = tDay - 273;
        String formattedDouble = String.format("%.2f", this.tDay);
        this.tDay = Double.parseDouble(formattedDouble);
        return this.tDay;
    }

    public void settDay(double tDay) {
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
