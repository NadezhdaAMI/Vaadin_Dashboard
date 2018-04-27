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

		repository.deleteAll();

		MainUI.counter = new Counter(0);
		MainUI.counter.incCounter();
		repository.save(MainUI.counter);

		for (Counter counter : repository.findAll()) {
			System.out.println("Counter!" + counter);
		}
		System.out.println("findById: " + repository.findAll().get(0));
		System.out.println();
	}
}

