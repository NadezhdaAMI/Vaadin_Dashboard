package ru.mitina.vaadin.app;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;

import static org.springframework.http.HttpHeaders.USER_AGENT;


public class MainService {

    private static final Logger log = LogManager.getLogger(MainService.class);

    public static String jsonToString(String url) throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = null;
        try {
            request = new HttpGet(url);
            request.addHeader("User-Agent", USER_AGENT);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
            log.error("Ошибка при инициализации запроса через url!");
        }
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("При выполнении запроса клиента произошла ошибка!");
        }
        BufferedReader rd = null;
        try {
            if (response != null) {
                rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Json файл не сохранен в строку!");
        }

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd != null ? rd.readLine() : null) != null) { //complicated
            result.append(line);
        }

        log.info("Json файл сохранен в строку");
        return result.toString();
    }
}
