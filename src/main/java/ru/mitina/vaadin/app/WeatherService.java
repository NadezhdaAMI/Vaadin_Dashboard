package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.http.HttpHeaders.USER_AGENT;

public class WeatherService {

    private static final String beginURL = "http://api.openweathermap.org/data/2.5/forecast?id=";

    private static final String endURL = "&APPID=fd05633695761f3e038d2270479047af";

    public static String url = "http://api.openweathermap.org/data/2.5/forecast?id=1496747&APPID=fd05633695761f3e038d2270479047af"; // Novosibirsk

    private static Map<Integer, String> map = new TreeMap<>();

    static {
        map.put(498817, "Санкт-Петербург");
        map.put(524901, "Москва");
        map.put(1496747, "Новосибирск");
    }

    public static Map<Integer, String> getMap() {
        return map;
    }


    public static String jsonToString() throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    public static Weather paramToday(){

        String jsonWeather = null;
        try {
            jsonWeather = WeatherService.jsonToString();
        } catch (IOException exp) {
            exp.printStackTrace();
        }

        Weather day = new Weather();
        day.settDay(JsonPath.read(jsonWeather, "$.list[2].main.temp")); // состояние на 15:00
        day.settNigth(JsonPath.read(jsonWeather, "$.list[6].main.temp")); // состояние на 3:00! (следующий день, 3:00)
        day.setWindSpeed(JsonPath.read(jsonWeather, "$.list[2].wind.speed")); // состояние на 3:00! (следующий день, 3:00)

        return day;
    }

    public static Weather paramTomor(){

        String jsonWeather = null;
        try {
            jsonWeather = WeatherService.jsonToString();
        } catch (IOException exp) {
            exp.printStackTrace();
        }

        Weather day = new Weather();
        day.settDay(JsonPath.read(jsonWeather, "$.list[10].main.temp")); // состояние на 15:00 завтрашнего дня
        day.settNigth(JsonPath.read(jsonWeather, "$.list[14].main.temp")); // состояние на 3:00! (следующий день, 3:00)
        day.setWindSpeed(JsonPath.read(jsonWeather, "$.list[10].wind.speed")); // состояние на 3:00! (следующий день, 3:00)

        return day;
    }

    public static void fillItems(VerticalLayout dayitem){

        HorizontalLayout dayToday = new HorizontalLayout();
        dayToday.setStyleName("layoutDayItem");
        DateFormat dateFormatday = new SimpleDateFormat("E', ' dd.MM ");
        Date date = new Date();
        Label todayInfo = new Label(dateFormatday.format(date)); // сегодня
        dayToday.addComponent(todayInfo);

        VerticalLayout dayOptions = new VerticalLayout();
        dayOptions.setMargin(false);

        dayOptions.addComponent(new Label("днем " + WeatherService.paramToday().gettDay() + " C"));
        dayOptions.addComponent(new Label("ночью " + WeatherService.paramToday().gettNigth() + " C"));
        dayOptions.addComponent(new Label("ветер " + WeatherService.paramToday().getWindSpeed() + " м/с"));
        dayToday.addComponent(dayOptions);

        //tomorrow item
        HorizontalLayout dayTomorrow = new HorizontalLayout();
        dayTomorrow.setStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        Label tomorrowInfo = new Label(dateFormatday.format(dateT)); // завтра
        dayTomorrow.addComponent(tomorrowInfo);

        VerticalLayout tomOptions = new VerticalLayout();
        tomOptions.setMargin(false);

        tomOptions.addComponent(new Label("днем " + WeatherService.paramTomor().gettDay() + " C"));
        tomOptions.addComponent(new Label("ночью " + WeatherService.paramTomor().gettNigth() + " C"));
        tomOptions.addComponent(new Label("ветер " + WeatherService.paramTomor().getWindSpeed() + " м/с"));
        dayTomorrow.addComponent(tomOptions);

        dayitem.addComponent(dayToday);
        dayitem.addComponent(dayTomorrow);

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
    }
}
