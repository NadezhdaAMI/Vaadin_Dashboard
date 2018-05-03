package ru.mitina.vaadin.app;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * Класс содержит общий метод для сохранения файла json по заданному url в строку
 */

public class MainService {

    private static final Logger log = LogManager.getLogger(MainService.class);

    /** метод для сохранения файла json по заданному url в строку
     * @param url  url публичных информеров с данными в формате json
    */
    public static String jsonToString(String url) throws ClassCastException{

        String res = "";
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            res = result.toString();
            log.info("Json файл сохранен в строку");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("При выполнении запроса клиента произошла ошибка!");
        }
        return res;
    }
}
