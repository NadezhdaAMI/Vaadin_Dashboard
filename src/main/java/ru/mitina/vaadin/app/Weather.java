package ru.mitina.vaadin.app;

/**
 * Класс для описания параметров погоды
 *
 * */
public class Weather {

    /** Текущая температура*/
    private String tDay;

    /** Температура ночью*/
    private String tNight;

    /** Скорость ветра*/
    private String wind;

    /** Давление в мм рт. ст.*/
    private String pressure;

    /** Влажность*/
    private String humidity;

    /** Иконка для отображения текущего состояния погодных условий */
    private String icon;

    public String gettDay() {
        double d = Double.parseDouble(tDay);
        return String.format("%.0f", d);
    }

    public void settDay(String tDay) {
        this.tDay = tDay;
    }

    public String gettNight() {
        double d = Double.parseDouble(tNight);
        return String.format("%.0f", d);
    }

    public void settNight(String tNight) {
        this.tNight = tNight;
    }

    public String getWind() {
        double d = Double.parseDouble(wind);
        return String.format("%.0f", d);
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getPressure() {
        double d = Double.parseDouble(pressure);
        /* Перевод из паскалей в мм рт. ст. */
        double res = d/1.333224;
        return String.format("%.0f", res);
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        double d = Double.parseDouble(humidity);
        return String.format("%.0f", d);
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

