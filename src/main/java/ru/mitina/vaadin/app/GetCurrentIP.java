package ru.mitina.vaadin.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

public class GetCurrentIP {

    private static final Logger log = LogManager.getLogger(GetCurrentIP.class);

    public static String getClientIp() {

        log.info("Получение IP адреса клиента...");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ip = request.getRemoteAddr();
        return ip;
    }

    public static String getServerIp() {
        log.info("Получение IP адреса сервера...");
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
                    log.error("Ваш IP адрес не доступен!");
                }
            }
            log.info("Ваш IP адрес получен");
        } catch (Exception ex) {

            try {
                InetAddress ip = InetAddress.getLocalHost();
                ipAddress = (ip.getHostAddress()).trim();
            } catch(Exception exp) {
                ipAddress = "ERROR";
                log.error("Ваш IP адрес не доступен!");
            }
        }
        return ipAddress;
    }
}
