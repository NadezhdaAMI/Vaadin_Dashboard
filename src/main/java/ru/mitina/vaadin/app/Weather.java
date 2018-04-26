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

        return tDay;
    }

    public void settDay(double tDay) {
        this.tDay = tDay - 273;
        String formattedDouble = String.format("%.1f", this.tDay);
        this.tDay = Double.parseDouble(formattedDouble);
    }

    public double gettNigth() {
        return tNigth;
    }

    public void settNigth(double tNigth) {
        this.tNigth = tNigth;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
