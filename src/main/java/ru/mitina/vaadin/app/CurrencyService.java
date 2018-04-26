package ru.mitina.vaadin.app;


import com.jayway.jsonpath.JsonPath;
import com.vaadin.ui.Grid;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.USER_AGENT;


public class CurrencyService {

    public static String url = "https://www.cbr-xml-daily.ru/daily_json.js";

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

    public static List<Currency> getGridContent(){
        String jsonMoney = null;
        try {
            jsonMoney = CurrencyService.jsonToString();
        } catch (IOException exp) {
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

        // Have some data
        List<Currency> valute = Arrays.asList(usd, eur);
        return valute;
    }

    public static Grid fillGrid(Grid<Currency> grid){

        grid.setItems(CurrencyService.getGridContent());
        grid.addColumn(Currency::getName).setCaption("Валюта");
        grid.addColumn(Currency::getValue).setCaption("Сегодня");
        grid.addColumn(Currency::getPrevious).setCaption("Вчера");

        return grid;
    }
}
