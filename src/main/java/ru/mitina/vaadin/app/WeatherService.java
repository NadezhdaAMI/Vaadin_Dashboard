package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class WeatherService {

    public static final String beginURL = "http://api.openweathermap.org/data/2.5/weather?id=";

    public static final String beginURL2 = "http://api.openweathermap.org/data/2.5/forecast?id=";

    public static final String endURL = "&APPID=fd05633695761f3e038d2270479047af&units=metric";

    private static String urlTod = "http://api.openweathermap.org/data/2.5/weather?id=1496747&APPID=fd05633695761f3e038d2270479047af&units=metric";

    private static String urlTom = "http://api.openweathermap.org/data/2.5/forecast?id=1496747&APPID=fd05633695761f3e038d2270479047af&units=metric";

    private static Map<Integer, String> map = new TreeMap<>();

    private static final Logger log = LogManager.getLogger(WeatherService.class);

    private static String jsonToday = null;

    private static String jsonTom = null;

    static {
        map.put(498817, "Санкт-Петербург");
        map.put(524901, "Москва");
        map.put(1496747, "Новосибирск");
        setJsonWeather();
    }

    public static Map<Integer, String> getMap() {
        return map;
    }

    public static String getJsonToday() {
        return jsonToday;
    }

    public static void setJsonWeather() {
        try {
            jsonToday = MainService.jsonToString(urlTod);
            log.info("Данные с сайта " + urlTod +" сохранились в строку jsonToday");
            jsonTom = MainService.jsonToString(urlTom);
            log.info("Данные с сайта " + urlTom + " сохранились в строку jsonTom");
        } catch (IOException exp) {
            log.error("Данные с сайта не сохранились в строку!");
            exp.printStackTrace();
        }
    }

    public static Weather paramToday(Weather day){
        log.info(jsonToday);
        day.settDay(JsonPath.read(jsonToday, "$.main.temp").toString());
        day.setWindSpeed(JsonPath.read(jsonToday, "$.wind.speed").toString());
        day.setHumidity(JsonPath.read(jsonToday, "$.main.humidity").toString());
        day.setPressure(JsonPath.read(jsonToday, "$.main.pressure").toString());
        day.setIcon(JsonPath.read(jsonToday, "$.weather[0].icon").toString());
        log.info("Заполнен layout сегодняшнего дня");
        return day;
    }

    public static Weather paramTomor(Weather day){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long midnight = c.getTimeInMillis();

        int n = 18;
        double tmax = 0;
        double tmin = 0;
        double windSum = 0;
        double presSum = 0;
        double humSum = 0;
        int k = 0;

        for (int i = 0; i < n; i++) {
            if (Long.parseLong(JsonPath.read(jsonTom, "$.list[" + i + "].dt").toString()) >= midnight){
                double t1 = Double.parseDouble(JsonPath.read(jsonTom, "$.list[" + i + "].main.temp").toString());
                tmax = (t1 > tmax ? t1 : tmax);
                tmin = (t1 < tmin ? t1 : tmin);
                windSum = windSum + Double.parseDouble(JsonPath.read(jsonTom, "$.list[" + i + "].wind.speed").toString());
                presSum = presSum + Double.parseDouble(JsonPath.read(jsonTom, "$.list[" + i + "].main.pressure").toString());
                humSum = humSum + Double.parseDouble(JsonPath.read(jsonTom, "$.list[" + i + "].main.humidity").toString());
                k++;
            }
        }
        double windSpeed = windSum / k;
        double pressure = presSum / k;
        double humidity = humSum / k;
        day.settDay(String.valueOf(tmax));
        day.settNigth(String.valueOf(tmin));
        day.setWindSpeed(String.valueOf(windSpeed));
        day.setPressure(String.valueOf(pressure));
        day.setHumidity(String.valueOf(humidity));

        log.info("Заполнен layout завтрашнего дня");
        return day;
    }

    public static void fillItems(VerticalLayout dayitem){

        log.info("Заполняется layout сегодняшнего дня");
        HorizontalLayout dayToday = new HorizontalLayout();
        dayToday.setStyleName("layoutDayItem");
        VerticalLayout v1 = new VerticalLayout();
        v1.setMargin(false);
        v1.setHeight("20px");
        DateFormat dateFormatday = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Date date = new Date();
        v1.addComponent(new Label(dateFormatday.format(date)));
        Weather day = new Weather();

        paramToday(day);

        v1.addComponent(new Label("сегодня"));
        Image image = showIcon(day.getIcon());
        v1.addComponent(image);
        image.setStyleName("delIndent");
        dayToday.addComponent(v1);

        VerticalLayout v2 = new VerticalLayout();
        v2.setMargin(false);
        v2.setHeight("100px");
        Label lab1 = new Label("" + day.gettDay() + " C");
        lab1.addStyleName("textSizeToday");
        v2.addComponent(lab1);
        v2.setComponentAlignment(lab1, Alignment.MIDDLE_CENTER);

        VerticalLayout v3 = new VerticalLayout();
        v3.setMargin(false);
        v3.addComponent(new Label("ветер " + day.getWindSpeed() + " м/с"));
        v3.addComponent(new Label("давл " + day.getPressure() + " мм"));
        v3.addComponent(new Label("влажн " + day.getHumidity() + " %"));

        dayToday.addComponent(v2);
        dayToday.addComponent(v3);

        log.info("Заполняется layout завтрашнего дня");
        HorizontalLayout dayTomorrow = new HorizontalLayout();

        fillItemTom(dayTomorrow);

        dayitem.addComponent(dayToday);
        dayitem.addComponent(dayTomorrow);
        log.info("Заполнены layout-ы каждого дня данными о погоде");
    }

    public static void fillItemTom(HorizontalLayout dayTom){
        dayTom.setStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        DateFormat dateFormatday = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Label tomorrowInfo = new Label(dateFormatday.format(dateT)); // завтра
        dayTom.addComponent(tomorrowInfo);

        VerticalLayout tomOptions = new VerticalLayout();
        tomOptions.setMargin(false);

        Weather tom = new Weather();
        paramTomor(tom);

        Label lab2 = new Label("мин " + tom.gettDay() + " C");
        tomOptions.addComponent(lab2);
        tomOptions.addComponent(new Label("макс " + tom.gettNigth() + " C"));

        VerticalLayout t3 = new VerticalLayout();
        t3.setMargin(false);
        t3.addComponent(new Label("ветер " + tom.getWindSpeed() + " м/с"));
        t3.addComponent(new Label("давл " + tom.getPressure() + " мм"));
        t3.addComponent(new Label("влажн " + tom.getHumidity() + " %"));

        dayTom.addComponent(tomOptions);
        dayTom.addComponent(t3);
    }

    public static void buildUrl(String cityName, String beginURL, String endURL, String req){
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
        if (req == WeatherService.getUrlTod()){
            StringBuilder s = new StringBuilder();
            String res = s.append(beginURL).append(cityId).append(endURL).toString();
            log.info("Получен " + res + " с учетом id города");
            setUrlTod(res);
        }
        if (req == WeatherService.getUrlTom()){
            StringBuilder s = new StringBuilder();
            String res2 = s.append(beginURL2).append(cityId).append(endURL).toString();
            log.info("Получен " + res2 + " с учетом id города");
            setUrlTom(res2);
        }
    }

    public static Image showIcon(String icon){
        ThemeResource resource = new ThemeResource("icons/" + icon + ".png");
        Image im = new Image("", resource);
        im.setHeight("80px");
        im.setWidth("80px");
        log.info("Получена иконка");
        return im;
    }

    public static String getUrlTod() {
        return urlTod;
    }

    public static void setUrlTod(String urlTod) {
        WeatherService.urlTod = urlTod;
    }

    public static String getUrlTom() {
        return urlTom;
    }

    public static void setUrlTom(String urlTom) {
        WeatherService.urlTom = urlTom;
    }
}
