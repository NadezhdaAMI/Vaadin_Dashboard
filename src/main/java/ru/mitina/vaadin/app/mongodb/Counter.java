package ru.mitina.vaadin.app.mongodb;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "countservice")
public class Counter {

    @Id
    private String id;

    private int count;

    public Counter() {
    }

    public Counter(int count) {
        this.count = count;
    }

    public void incCounter(){
        count++;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "" + count;
    }
}