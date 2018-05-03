package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  Класс для получения данных о погоде из публичных информеров
 */
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
    private static String cityName;

    static {
        map.put(498817, "Санкт-Петербург");
        map.put(524901, "Москва");
        map.put(1496747, "Новосибирск");
    }

    public static Map<Integer, String> getMap() {
        return map;
    }

    public static String getJsonToday() {
        return jsonToday;
    }

    /** Метод для сохранения файла json в строку по заданному url*/
    public static void setJsonWeather() {
        try {
            jsonToday = MainService.jsonToString(urlTod);
            log.info("Данные с сайта " + urlTod +" сохранились в строку jsonToday");
            jsonTom = MainService.jsonToString(urlTom);
            log.info("Данные с сайта " + urlTom + " сохранились в строку jsonTom");
        } catch (ClassCastException exp) {
            log.error("Данные с сайта не сохранились в строку!");
            exp.printStackTrace();
        }
    }

    /** Метод заполнения параметров погоды для текущего дня
     * @param day текущий день
     */
    public static Weather paramToday(Weather day) {
        try {
            log.info(jsonToday);
            day.settDay(JsonPath.read(jsonToday, "$.main.temp").toString());
            day.setWind(JsonPath.read(jsonToday, "$.wind.speed").toString());
            day.setHumidity(JsonPath.read(jsonToday, "$.main.humidity").toString());
            day.setPressure(JsonPath.read(jsonToday, "$.main.pressure").toString());
            day.setIcon(JsonPath.read(jsonToday, "$.weather[0].icon").toString());
            log.info("Заполнен layout сегодняшнего дня");
        }
        catch (PathNotFoundException ex){
            ex.printStackTrace();
            log.error("Не найден url для сохранения jsona в строку!");
        }
        return day;
    }

    /** Метод вычисления средних значений скорости ветра, давления и влажности
     * и минимальной и максимальной температуры завтрашнего дня
     * Поскольку в json файле для завтрашнего дня хранятся значения погоды для каждых трех часов
     * и каждые 3 часа меняется время в файле, с которого начинается отсчет, то находим
     * количество прогнозов и средние значения по завтрашним суткам
     * для каждого параметра (скорость ветра, давление, влажность)
     * @param day  завтрашний день
     */
    public static Weather paramTomor(Weather day){

        /** Получение даты завтрашнего дня и установление значения времени в полночь*/
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long midnight = c.getTimeInMillis() / 1000;

        int n = 18;
        double tmax = -70;
        double tmin = 70;
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
        log.info("midnight = " + midnight);
        log.info(Long.parseLong(JsonPath.read(jsonTom, "$.list[" + 0 + "].dt").toString()));
        log.info("k = " + k);
        double wind = windSum / k;
        double pressure = presSum / k;
        double humidity = humSum / k;
        day.settDay(String.valueOf(tmax));
        day.settNigth(String.valueOf(tmin));
        day.setWind(String.valueOf(wind));
        day.setPressure(String.valueOf(pressure));
        day.setHumidity(String.valueOf(humidity));
        log.info("Заполнен layout завтрашнего дня");
        return day;
    }

    /** Метод для заполнения вертикального layout-а
     * @param dayitem содержит два горизонтальных layout-а с данными о сегодняшнем и завтрашнем дне
     * {@link WeatherService#fillItemTom(HorizontalLayout tL)}
     */
    public static void fillItems(VerticalLayout dayitem){

        log.info("Заполняется layout сегодняшнего дня");
        HorizontalLayout todL = new HorizontalLayout();
        todL.setStyleName("layoutDayItem");
        todL.setSpacing(true);
        VerticalLayout v1 = new VerticalLayout();
        v1.setMargin(false);
        v1.setSpacing(false);
        v1.setHeight("20px");
        DateFormat dateFormatday = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Date date = new Date();
        v1.addComponent(new Label(dateFormatday.format(date)));
        Weather day = new Weather();

        paramToday(day);

        Label text = new Label("сегодня");
        text.setStyleName("textSize");
        v1.addComponent(text);

        Image image = showIcon(day.getIcon());
        v1.addComponent(image);
        image.setStyleName("delIndent");
        todL.addComponent(v1);

        VerticalLayout v2 = new VerticalLayout();
        v2.setMargin(false);
        v2.setHeight("100px");
        Label lab1 = new Label("" + day.gettDay() + " C");
        lab1.addStyleName("textSizeToday");
        v2.addComponent(lab1);
        v2.setComponentAlignment(lab1, Alignment.MIDDLE_CENTER);

        VerticalLayout v3 = new VerticalLayout();
        v3.setMargin(false);
        v3.addComponent(new Label("ветер " + day.getWind() + " м/с"));
        v3.addComponent(new Label("давл " + day.getPressure() + " мм"));
        v3.addComponent(new Label("влажн " + day.getHumidity() + " %"));

        todL.addComponent(v2);
        todL.addComponent(v3);

        log.info("Заполняется layout завтрашнего дня");
        HorizontalLayout tomL = new HorizontalLayout();
        tomL.setSpacing(true);

        fillItemTom(tomL);
        dayitem.addComponent(todL);
        dayitem.addComponent(tomL);
        log.info("Заполнены layout-ы каждого дня данными о погоде");
    }

    /** Метод для заполнения горизонтального layout-а завтрашнего дня
     * @param tL  горизонтальный layout с параметрами погоды для завтрашнего дня
     */
    public static void fillItemTom(HorizontalLayout tL){
        tL.setStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        DateFormat formatd = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Label tomInfo = new Label(formatd.format(dateT));
        tL.addComponent(tomInfo);

        VerticalLayout tomOptions = new VerticalLayout();
        tomOptions.setMargin(false);

        Weather tom = new Weather();
        paramTomor(tom);

        Label lab2 = new Label("макс " + tom.gettDay() + " C");
        lab2.setStyleName("textSize");
        tomOptions.addComponent(lab2);
        Label lab3 = new Label("мин " + tom.gettNigth() + " C");
        lab3.setStyleName("textSize");
        tomOptions.addComponent(lab3);
        tomOptions.setSpacing(false);

        VerticalLayout t3 = new VerticalLayout();
        t3.setMargin(false);
        t3.addComponent(new Label("ветер " + tom.getWind() + " м/с"));
        t3.addComponent(new Label("давл " + tom.getPressure() + " мм"));
        t3.addComponent(new Label("влажн " + tom.getHumidity() + " %"));

        tL.addComponent(tomOptions);
        tL.addComponent(t3);
        tL.setComponentAlignment(t3, Alignment.TOP_RIGHT);
    }

    /** Метод для создания url с учетом id города
     *  в зависимости от дня (сегодняшнего или завтрашнего)
     *  @param cityNameL имя города, для которого смотрим погоду
     *  @param beginURL первая часть url строки без id города
     *  @param endURL вторая часть url строки
     *  @param req полный url c учетом id города и передаваемого запроса для сегодняшнего или
     *  завтрашнего дня
     */
    public static void buildUrl(String cityNameL, String beginURL, String endURL, String req){
        int cityId = 0;
        Collection<Integer> collection = map.keySet();
        for (Integer key : collection) {
            Object obj = map.get(key);
            if (key != null) {
                if (cityNameL.equals(obj)) {
                    cityId = key;
                    break;
                }
            }
        }
        if (req.equals(WeatherService.getUrlTod())){
            String res = beginURL+ cityId + endURL;
            log.info("Получен " + res + " с учетом id города");
            setUrlTod(res);
        }
        if (req.equals(WeatherService.getUrlTom())){
            String res2 = beginURL2 + cityId + endURL;
            log.info("Получен " + res2 + " с учетом id города");
            setUrlTom(res2);
        }
    }

    /** Метод для отображения иконки текущих погодных условий
     * @param icon  иконка для отображения текущих погодных условий
     */
    public static Image showIcon(String icon){
        ThemeResource resource = new ThemeResource("icons/" + icon + ".png");
        Image im = new Image("", resource);
        im.setHeight("70px");
        im.setWidth("70px");
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

    private static void setUrlTom(String urlTom) {
        WeatherService.urlTom = urlTom;
    }

    public static void setCityName(String name){
        cityName = name;
    }

    public static String getCityName() {
        return cityName;
    }
}
