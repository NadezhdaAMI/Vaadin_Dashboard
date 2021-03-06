package ru.mitina.vaadin.app;


import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.vaadin.ui.Grid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  Класс для получения данных о валюте из публичных информеров
 */
public class CurrencyService {

    /** URL_C - url для получения курса валют Центробанка РФ */
    private static final String URL_C = "https://www.cbr-xml-daily.ru/daily_json.js";

    /** URL_S(123) - для получения стоимости покупки и продажи валюты в Сбербанке */
    private static final String URL_S1 = "http://www.sberbank.ru/common%2Fjs%2Fget_quote_values.php%3Fversion%3D1%26inf_block%3D123%26_number_amount114%3D10000%26qid%5B%5D=3%26qid%5B%5D=2%26cbrf%3D0%26period%3Don%26_date_afrom114%3D";
    private static final String URL_S2 = "%26_date_ato114%3D";
    private static final String URL_S3 = "%26mode%3Dfull%26display%3Djson";

    private static final Logger LOG = LogManager.getLogger(WeatherService.class);

    /** Метод для получения контента для grid
     * @return val массив валют
     */
    private static List<Currency> getGridContent(){

        String jsonCbr;
        String jsonSber;
        List<Currency> val = null;
        try {
            jsonCbr = MainService.jsonToString(URL_C);
            String urlS = buildUrl();
            jsonSber = MainService.jsonToString(urlS);
            LOG.info("json с сайта валют сохранен в строку");

            Currency usd = new Currency();
            usd.setName(JsonPath.read(jsonCbr, "$.Valute.USD.CharCode").toString() + "/RUB");
            usd.setValue(JsonPath.read(jsonCbr, "$.Valute.USD.Value").toString());
            usd.setBuy(JsonPath.read(jsonSber, "$.3.quotes.*.buy").toString().substring(2, 7));
            usd.setSell(JsonPath.read(jsonSber, "$.3.quotes.*.sell").toString().substring(2, 7));

            Currency eur = new Currency();
            eur.setName(JsonPath.read(jsonCbr, "$.Valute.EUR.CharCode").toString() + "/RUB");
            eur.setValue(JsonPath.read(jsonCbr, "$.Valute.EUR.Value").toString());
            eur.setBuy(JsonPath.read(jsonSber, "$.2.quotes.*.buy").toString().substring(2, 7));
            eur.setSell(JsonPath.read(jsonSber, "$.2.quotes.*.sell").toString().substring(2, 7));

            val = Arrays.asList(usd, eur);
            LOG.info("получен контент для grid");
        } catch (PathNotFoundException exp) {
            LOG.error("элементы для контента не получены!");
        }
        return val;
    }

    /** Метод заполнения grid полученным контентом
     * @see CurrencyService#getGridContent()
     * @param grid  таблица, для заполнения данными о валютах
     */
    public static void fillGrid(Grid<Currency> grid){
        try {
            grid.setItems(CurrencyService.getGridContent());
            grid.addColumn(Currency::getName).setCaption("Валюта");
            grid.addColumn(Currency::getValue).setCaption("КУРС ЦБ");
            grid.addColumn(Currency::getBuy).setCaption("ПОКУПКА");
            grid.addColumn(Currency::getSell).setCaption("ПРОДАЖА");
            LOG.info("grid заполнена данными по валюте");
        }
        catch (PathNotFoundException ex){
            LOG.error("Не найден url для сохранения jsona в строку!");
        }
    }

    /** Метод создания url с учетом текущей даты
     * @return res url c учетом текущей даты
     */
    private static String buildUrl(){
        StringBuilder s = new StringBuilder();
        DateFormat formatD = new SimpleDateFormat("dd.MM.yyyy");
        String dateF = formatD.format(new Date());
        String res = s.append(URL_S1).append(dateF).append(URL_S2).append(dateF).append(URL_S3).toString();
        LOG.info("Получен url для jsona c данными по валюте текущего дня " + dateF);
        return res;
    }
}
