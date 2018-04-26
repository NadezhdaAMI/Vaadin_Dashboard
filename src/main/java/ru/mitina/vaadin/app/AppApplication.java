package ru.mitina.vaadin.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static ru.mitina.vaadin.app.CurrencyService.downloadUsingStream;


@SpringBootApplication
public class AppApplication {

	private static final Logger logger = LogManager.getLogger(AppApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);

		logger.info("Entering application!");



	}
}
