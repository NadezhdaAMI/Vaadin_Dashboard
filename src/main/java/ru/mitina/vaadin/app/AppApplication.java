package ru.mitina.vaadin.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@EnableAutoConfiguration
@SpringBootApplication
public class AppApplication implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(AppApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
		logger.info("Entering application!");
	}

	@Override
	public void run(String... args) throws Exception {
	}
}

