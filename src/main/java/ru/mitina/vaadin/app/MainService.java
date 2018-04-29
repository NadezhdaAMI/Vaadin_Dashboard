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
        HttpGet request = new HttpGet(url);

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
            log.info("Json файл не сохранен в строку!");
        }

        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        log.info("Json файл сохранен в строку");
        return result.toString();
    }
}
