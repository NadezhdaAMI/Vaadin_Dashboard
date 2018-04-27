package ru.mitina.vaadin.app;


import com.jayway.jsonpath.JsonPath;
import com.vaadin.ui.Grid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;


public class CurrencyService {

    public static String url = "https://www.cbr-xml-daily.ru/daily_json.js";

    private static final Logger log = LogManager.getLogger(WeatherService.class);

    public static List<Currency> getGridContent(){

        String jsonMoney = null;
        try {
            jsonMoney = MainService.jsonToString(url);
            log.info("json с сайта валют сохранен в строку");
        } catch (IOException exp) {
            log.error("json с сайта валют не сохранен в строку!");
            exp.printStackTrace();
        }

        Currency usd = new Currency();
        usd.setName(JsonPath.read(jsonMoney, "$.Valute.USD.CharCode"));
        usd.setValue(JsonPath.read(jsonMoney, "$.Valute.USD.Value"));
        usd.setPrevious(JsonPath.read(jsonMoney, "$.Valute.USD.Previous"));

        Currency eur = new Currency();
        eur.setName(JsonPath.read(jsonMoney, "$.Valute.EUR.CharCode"));
        eur.setValue(JsonPath.read(jsonMoney, "$.Valute.EUR.Value"));
        eur.setPrevious(JsonPath.read(jsonMoney, "$.Valute.EUR.Previous"));

        List<Currency> valute = Arrays.asList(usd, eur);
        log.info("получен контент для grid");

        return valute;
    }

    public static Grid fillGrid(Grid<Currency> grid){

        grid.setItems(CurrencyService.getGridContent());
        grid.addColumn(Currency::getName).setCaption("Валюта");
        grid.addColumn(Currency::getValue).setCaption("Сегодня");
        grid.addColumn(Currency::getPrevious).setCaption("Вчера");
        log.info("grid заполнена данными по валюте");
        return grid;
    }
}
