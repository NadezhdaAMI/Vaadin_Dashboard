package ru.mitina.vaadin.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@SpringBootApplication
public class AppApplication {

	private static final Logger logger = LogManager.getLogger(AppApplication.class);

	public static InetAddress ip;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);

		String hostname;

		logger.info("Entering application!");

		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("Your current IP address : " + ip.getHostAddress());
			System.out.println("Your current Hostname : " + hostname);

		} catch (UnknownHostException e) {
			logger.error("Your current IP address is not available!");
			e.printStackTrace();
		}

	}
}
