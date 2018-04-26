package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.springframework.http.HttpHeaders.USER_AGENT;

public class WeatherService {

    public static String url = "http://api.openweathermap.org/data/2.5/forecast?id=1496747&APPID=fd05633695761f3e038d2270479047af"; // Novosibirsk

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

    public static Weather paramDay(){

        String jsonWeather = null;
        try {
            jsonWeather = WeatherService.jsonToString();
        } catch (IOException exp) {
            exp.printStackTrace();
        }

        Weather day = new Weather();
        day.settDay(JsonPath.read(jsonWeather, "$.list[0].main.temp"));

        return day;
    }
}
