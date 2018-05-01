package ru.mitina.vaadin.app;


import com.jayway.jsonpath.JsonPath;
import com.vaadin.ui.Grid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.Arrays;
import java.util.List;


public class CurrencyService {

    private static String urlC = "https://www.cbr-xml-daily.ru/daily_json.js";

    private static String urlS = "http://www.sberbank.ru/common%2Fjs%2Fget_quote_values.php%3Fversion%3D1%26inf_block%3D123%26_number_amount114%3D10000%26qid%5B%5D=3%26qid%5B%5D=2%26cbrf%3D0%26period%3Don%26_date_afrom114%3D01.05.2018%26_date_ato114%3D01.05.2018%26mode%3Dfull%26display%3Djson";

    private static final Logger log = LogManager.getLogger(WeatherService.class);

    public static List<Currency> getGridContent(){

        String jsonCbr = null;
        String jsonSber = null;
        try {
            jsonCbr = MainService.jsonToString(urlC);
            jsonSber = MainService.jsonToString(urlS);
            log.info("json с сайта валют сохранен в строку");
        } catch (IOException exp) {
            log.error("json с сайта валют не сохранен в строку!");
            exp.printStackTrace();
        }

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

        List<Currency> valute = Arrays.asList(usd, eur);
        log.info("получен контент для grid");

        return valute;
    }

    public static Grid fillGrid(Grid<Currency> grid){

        grid.setItems(CurrencyService.getGridContent());
        grid.addColumn(Currency::getName).setCaption("Валюта");
        grid.addColumn(Currency::getValue).setCaption("КУРС ЦБ");
        grid.addColumn(Currency::getBuy).setCaption("ПОКУПКА");
        grid.addColumn(Currency::getSell).setCaption("ПРОДАЖА");
        log.info("grid заполнена данными по валюте");
        return grid;
    }
}
