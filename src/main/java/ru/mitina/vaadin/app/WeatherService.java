package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  Класс для получения данных о погоде из публичных информеров
 */
public class WeatherService {

    /** BEGIN_URL - получение текущей погоды, обновляется каждые 10 минут*/
    public static final String BEGIN_URL = "http://api.openweathermap.org/data/2.5/weather?id=";

    /** BEGIN_URL2 - получение погоды на завтра*/
    public static final String BEGIN_URL2 = "http://api.openweathermap.org/data/2.5/forecast?id=";

    /** END_URL - ключ для доступа к сервису*/
    public static final String END_URL = "&APPID=fd05633695761f3e038d2270479047af&units=metric";

    private static String urlTod = "http://api.openweathermap.org/data/2.5/weather?id=1496747&APPID=fd05633695761f3e038d2270479047af&units=metric";
    private static String urlTom = "http://api.openweathermap.org/data/2.5/forecast?id=1496747&APPID=fd05633695761f3e038d2270479047af&units=metric";

    private static Map<Integer, String> map = new TreeMap<>();

    private static final Logger LOG = LogManager.getLogger(WeatherService.class);

    private static String strToday = null;
    private static String strTom = null;
    private static String cityName;

    private static final String DEGREE  = "\u00b0";

    static {
        map.put(498817, "Санкт-Петербург");
        map.put(524901, "Москва");
        map.put(1496747, "Новосибирск");
    }

    public static Map<Integer, String> getMap() {
        return map;
    }

    /** Метод для сохранения файлов json в строки по заданному url*/
    public static void setJsonWeather() {
        try {
            strToday = MainService.jsonToString(urlTod);
            LOG.info("Данные с сайта " + urlTod +" сохранились в строку strToday");
            strTom = MainService.jsonToString(urlTom);
            LOG.info("Данные с сайта " + urlTom + " сохранились в строку strTom");
        } catch (ClassCastException exp) {
            LOG.error("Данные с сайта не сохранились в строку!");
        }
    }

    /** Метод заполнения параметров погоды для текущего дня,
     * сервер источника(openweathermap.org) обновляет данные текущего дня каждые 10 минут
     * @param day текущий день
     */
    private static void paramToday(Weather day) {
        try {
            LOG.info("Заполнение layout-а сегодняшнего дня");
            day.settDay(readJs("$.main.temp"));
            day.setWind(readJs("$.wind.speed"));
            day.setHumidity(readJs("$.main.humidity"));
            day.setPressure(readJs("$.main.pressure"));
            day.setIcon(readJs("$.weather[0].icon"));
            LOG.info("Заполнен layout сегодняшнего дня");
        }
        catch (PathNotFoundException ex){
            LOG.error("Не найден url для сохранения jsona в строку!");
        }
    }

    /** Метод нахождения и преобразования элемента в строку
     * @param q путь в строке к нужному элементу
     */
    private static String readJs(String q){
        String s = "";
        try {
            s = JsonPath.read(strToday, q).toString();
        }catch (Exception e){
            LOG.error("не удалось прочитать json");
        }
        return s;
    }

    /** Метод вычисления средних значений скорости ветра, давления и влажности
     * и минимальной и максимальной температуры завтрашнего дня
     * Поскольку в json файле для завтрашнего дня хранятся значения погоды для каждых трех часов
     * и каждые 3 часа меняется время в файле, с которого начинается отсчет, то находим
     * количество прогнозов и средние значения по завтрашним суткам
     * для каждого параметра (скорость ветра, давление, влажность)
     * @param day  завтрашний день
     */
    private static void paramTom(Weather day){

        /* Получение даты завтрашнего дня и установление значения времени в полночь*/
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long midnight = c.getTimeInMillis() / 1000;
        LOG.info("midnight = " + midnight);

        /* n < 17 максимальное кол-во 3х часовых интервалов, по которым происходит усреднение
        * n будет изменяться в зависимости от времени обращения к серверу
        */
        int n = 17, k = 0;
        double tmax = -70, tmin = 70;
        double windSum = 0, presSum = 0, humSum = 0;
        for (int i = 0; i < n; i++) {
            if (Long.parseLong(JsonPath.read(strTom, "$.list[" + i + "].dt").toString()) >= midnight){
                double t1 = strToD("main.temp", i);
                tmax = (t1 > tmax ? t1 : tmax);
                tmin = (t1 < tmin ? t1 : tmin);
                windSum += strToD("wind.speed", i);
                presSum += strToD("main.pressure", i);
                humSum += strToD("main.humidity", i);
                k++;
            }
        }
        /* Заполняем параметры погоды завтрашнего дня полученными средними значениями*/
        day.settDay(String.valueOf(tmax));
        day.settNight(String.valueOf(tmin));
        day.setWind(String.valueOf(windSum / k));
        day.setPressure(String.valueOf(presSum / k));
        day.setHumidity(String.valueOf(humSum / k));
        LOG.info("Заполнен layout завтрашнего дня");
    }

    /** Метод нахождения и преобразования элемента к типу double
     * @param q путь в строке к нужному элементу
     * @param i индекс в цикле
     */
    private static double strToD(String q, int i){
        double d = 0;
        try {
            d = Double.parseDouble(JsonPath.read(strTom, "$.list[" + i + "]." + q).toString());
        }catch (PathNotFoundException e){
            LOG.error("Неверно указан путь в строке к элементу");
        }
        return d;
    }

    /** Метод для заполнения вертикального layout-а
     * @param days содержит два горизонтальных layout-а с данными о сегодняшнем и завтрашнем дне
     */
    public static void fillItems(VerticalLayout days){
        LOG.info("Заполняется главный layout для двух дней");
        HorizontalLayout todL = new HorizontalLayout();
        HorizontalLayout tomL = new HorizontalLayout();
        tomL.setSpacing(true);
        fillItemTod(todL);
        fillItemTom(tomL);
        days.addComponent(todL);
        days.addComponent(tomL);
        LOG.info("Заполнены layout-ы каждого дня данными о погоде");
    }

    /** Метод для заполнения горизонтального layout-а текущего дня
     * @param todL  горизонтальный layout с параметрами погоды для текущего дня
     */
    private static void fillItemTod(HorizontalLayout todL){

        LOG.info("Заполняется layout сегодняшнего дня");
        todL.setStyleName("layoutDayItem");
        todL.setSpacing(true);
        VerticalLayout v1 = new VerticalLayout();
        v1.setSpacing(false);
        v1.setMargin(false);
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
        todL.addComponent(v1);

        VerticalLayout v2 = new VerticalLayout();
        v2.setStyleName("mystyleV2");
        Label lab1 = new Label("" + day.gettDay() + " " + DEGREE + "C");
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
        todL.setComponentAlignment(v3, Alignment.TOP_RIGHT);
    }

    /** Метод для заполнения горизонтального layout-а завтрашнего дня
     * @param tL  горизонтальный layout с параметрами погоды для завтрашнего дня
     */
    private static void fillItemTom(HorizontalLayout tL){

        LOG.info("Заполняется layout завтрашнего дня");
        tL.setStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        DateFormat formatd = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Label tomInfo = new Label(formatd.format(dateT));
        tL.addComponent(tomInfo);

        VerticalLayout tomOptions = new VerticalLayout();
        tomOptions.setMargin(false);

        Weather tom = new Weather();
        paramTom(tom);

        /* Считаем, что максимальная температура это дневная */
        Label lab2 = new Label("макс " + tom.gettDay() + " " + DEGREE + "C");
        lab2.setStyleName("textSize");
        tomOptions.addComponent(lab2);

        /* Считаем, что минимальная температура это ночная */
        Label lab3 = new Label("мин " + tom.gettNight() + " " + DEGREE + "C");
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

    /** Метод для создания url с учетом id города в зависимости от дня (сегодняшнего или завтрашнего)
     *  @param cityNameL имя города, для которого смотрим погоду
     *  @param beginURL первая часть url строки без id города
     *  @param endURL вторая часть url строки
     *  @param req полный url c учетом id города и передаваемого запроса для сегодняшнего или завтрашнего дня
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
        if (req.equals(WeatherService.getUrlTod())) setUrlTod(beginURL+ cityId + endURL);
        if (req.equals(WeatherService.getUrlTom())) setUrlTom(BEGIN_URL2 + cityId + endURL);
        LOG.info("Получен url с учетом id города");
    }

    /** Метод для отображения иконки текущих погодных условий
     * @param icon  иконка для отображения текущих погодных условий
     * @return im отформатированная иконка для вывода в layout
     */
    private static Image showIcon(String icon){
        ThemeResource resource = new ThemeResource("icons/" + icon + ".png");
        Image im = new Image("", resource);
        im.setStyleName("styleImage");
        LOG.info("Получена иконка");
        return im;
    }

    public static String getUrlTod() {
        return urlTod;
    }

    private static void setUrlTod(String urlTod) {
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
