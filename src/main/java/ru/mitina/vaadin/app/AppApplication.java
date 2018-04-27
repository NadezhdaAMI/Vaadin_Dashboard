package ru.mitina.vaadin.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ru.mitina.vaadin.app.mongodb.Counter;
import ru.mitina.vaadin.app.mongodb.CounterRepository;

@EnableAutoConfiguration
@SpringBootApplication

public class AppApplication implements CommandLineRunner {

	@Autowired
	public CounterRepository repository;

	private static final Logger logger = LogManager.getLogger(AppApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);

		logger.info("Entering application!");
	}

	@Override
	public void run(String... args) throws Exception {

//		repository.deleteAll();
//		MainUI.counter = new Counter(0);
//		repository.save(MainUI.counter);

		repository.findAll().get(0).getId();
		logger.info("get(0).getId() = " + repository.findAll().get(0).getId());
		logger.info("get(0) = " + repository.findAll().get(0));

		logger.info("до - " + repository.findAll().get(0));
		int n = repository.findAll().get(0).incCounter();
		logger.info("после - " + n);
		repository.deleteAll();
		Counter count = new Counter(n);
		repository.save(count);

		logger.info("get(0).getId() = " + repository.findAll().get(0).getId());
		logger.info("get(0) = " + repository.findAll().get(0));
		logger.info("size: " + repository.findAll().size());

		logger.info("Инициализация счетчика посещений");
	}
}

