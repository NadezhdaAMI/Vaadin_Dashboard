package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class WeatherService {

    private static final String beginURL = "http://api.openweathermap.org/data/2.5/weather?id=";

    private static final String endURL = "&APPID=fd05633695761f3e038d2270479047af&units=metric";

    public static String url = "http://api.openweathermap.org/data/2.5/weather?id=1496747&APPID=fd05633695761f3e038d2270479047af&units=metric"; // Novosibirsk

    private static Map<Integer, String> map = new TreeMap<>();

    private static final Logger log = LogManager.getLogger(WeatherService.class);

    private static String jsonWeather = null;

    static {
        map.put(498817, "Санкт-Петербург");
        map.put(524901, "Москва");
        map.put(1496747, "Новосибирск");

        setJsonWeather();
    }

    public static Map<Integer, String> getMap() {
        return map;
    }

    public static String getJsonWeather() {
        return jsonWeather;
    }

    public static void setJsonWeather() {
        try {
            jsonWeather = MainService.jsonToString(url);
            log.info("Данные с сайта сохранились в строку");
        } catch (IOException exp) {
            log.error("Данные с сайта не сохранились в строку!");
            exp.printStackTrace();
        }
    }

    public static Weather paramToday(Weather day){
        day.settDay(JsonPath.read(jsonWeather, "$.main.temp").toString()); // текущее состояние
//        day.settNigth(JsonPath.read(jsonWeather, "$.list[6].main.temp_min").toString()); // состояние на 3:00! (следующий день, 3:00)
        day.setWindSpeed(JsonPath.read(jsonWeather, "$.wind.speed").toString()); //текущее состояние
        day.setHumidity(JsonPath.read(jsonWeather, "$.main.humidity").toString());
        day.setPressure(JsonPath.read(jsonWeather, "$.main.pressure").toString());
        log.info("Заполнен layout сегодняшнего дня");
        return day;
    }

//    public static Weather paramTomor(Weather day){
//
//        day.settDay((JsonPath.read(jsonWeather, "$.list[10].main.temp")).toString()); // состояние на 15:00 завтрашнего дня
//        day.settNigth(JsonPath.read(jsonWeather, "$.list[14].main.temp_min").toString()); // состояние на 3:00! (следующий день, 3:00)
//        day.setWindSpeed(JsonPath.read(jsonWeather, "$.list[10].wind.speed").toString()); // состояние на 3:00! (следующий день, 3:00)
//        day.setHumidity(JsonPath.read(jsonWeather, "$.list[10].main.humidity").toString());
//        day.setPressure(JsonPath.read(jsonWeather, "$.list[10].main.pressure").toString());
//        log.info("Заполнен layout завтрашнего дня");
//        return day;
//    }

    public static void fillItems(VerticalLayout dayitem){

        //today item
        HorizontalLayout dayToday = new HorizontalLayout();
        dayToday.setStyleName("layoutDayItem");
        DateFormat dateFormatday = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Date date = new Date();
        Label todayInfo = new Label(dateFormatday.format(date)); // сегодня
        dayToday.addComponent(todayInfo);

        VerticalLayout dayOptions = new VerticalLayout();
        dayOptions.setMargin(false);

        Weather day = new Weather();
        paramToday(day);
        Label lab1 = new Label("" + day.gettDay() + " C");
        lab1.addStyleName("textSizeToday");
        dayOptions.addComponent(lab1);
//        dayOptions.addComponent(new Label("ночью " + day.gettNigth() + " C"));

        VerticalLayout dayOptions2 = new VerticalLayout();
        dayOptions2.setMargin(false);
        dayOptions2.addComponent(new Label("ветер " + day.getWindSpeed() + " м/с"));
        dayOptions2.addComponent(new Label("давл " + day.getPressure() + " Па"));
        dayOptions2.addComponent(new Label("влажн " + day.getHumidity() + " %"));
        dayToday.addComponent(dayOptions);
        dayToday.addComponent(dayOptions2);

//        //tomorrow item
//        HorizontalLayout dayTomorrow = new HorizontalLayout();
//        dayTomorrow.setStyleName("layoutDayItem");
//        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
//        Label tomorrowInfo = new Label(dateFormatday.format(dateT)); // завтра
//        dayTomorrow.addComponent(tomorrowInfo);
//
//        VerticalLayout tomOptions = new VerticalLayout();
//        tomOptions.setMargin(false);
//
//        Weather tom = new Weather();
////        paramTomor(tom);
//        Label lab2 = new Label("" + tom.gettDay() + " C");
//        tomOptions.addComponent(lab2);
//        lab2.addStyleName("textSizeToday");
//        tomOptions.addComponent(new Label("ночью " + tom.gettNigth() + " C"));
//
//        VerticalLayout tomOptions2 = new VerticalLayout();
//        tomOptions2.setMargin(false);
//
//        tomOptions2.addComponent(new Label("ветер " + tom.getWindSpeed() + " м/с"));
//        tomOptions2.addComponent(new Label("давл " + tom.getPressure() + " Па"));
//        tomOptions2.addComponent(new Label("влажн " + tom.getHumidity() + " %"));
//
//        dayTomorrow.addComponent(tomOptions);
//        dayTomorrow.addComponent(tomOptions2);

        dayitem.addComponent(dayToday);
//        dayitem.addComponent(dayTomorrow);
        log.info("Заполнены layout-ы каждого дня данными о погоде");
    }

    public static void buildUrl(String cityName){

        int cityId = 0;
        Collection<Integer> collection = map.keySet();
        for (Integer key : collection) {
            Object obj = map.get(key);
            if (key != null) {
                if (cityName.equals(obj)) {
                    cityId = key;
                    break;
                }
            }
        }
        StringBuilder s = new StringBuilder();
        s.append(beginURL);
        s.append(cityId);
        s.append(endURL);
        url = s.toString();
        log.info("Получен url с учетом id города");
    }
}
