package ru.mitina.vaadin.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class GetCurrentIP {

    private static final Logger log = LogManager.getLogger(GetCurrentIP.class);

    public static String getClientIp() {

        log.info("Получение IP адреса клиента...");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ip = request.getRemoteAddr();
        return ip;
    }
}
