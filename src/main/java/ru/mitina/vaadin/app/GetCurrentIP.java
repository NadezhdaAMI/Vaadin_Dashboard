package ru.mitina.vaadin.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

public class GetCurrentIP {

    private static final Logger log = LogManager.getLogger(GetCurrentIP.class);

    public static String getIpAddress() {

        log.info("Получение IP адреса...");
        URL url;
        BufferedReader in;
        String ipAddress;
        try {
            url = new URL("http://bot.whatismyipaddress.com");
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            ipAddress = in.readLine().trim();

            if (!(ipAddress.length() > 0)) {
                try {
                    InetAddress ip = InetAddress.getLocalHost();
                    ipAddress = (ip.getHostAddress()).trim();

                } catch(Exception exp) {
                    ipAddress = "ERROR";
                    log.error("Ваш текущий IP адрес не доступен!");
                }
            }
            log.info("Ваш текущий IP адрес получен");
        } catch (Exception ex) {

            try {
                InetAddress ip = InetAddress.getLocalHost();
                ipAddress = (ip.getHostAddress()).trim();
            } catch(Exception exp) {
                ipAddress = "ERROR";
                log.error("Ваш текущий IP адрес не доступен!");
            }
        }

        return ipAddress;
    }
}
