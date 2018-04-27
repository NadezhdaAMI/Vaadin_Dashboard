package ru.mitina.vaadin.app.mongodb;


import org.springframework.data.mongodb.repository.MongoRepository;


public interface CounterRepository extends MongoRepository<Counter, String>{

}