package ru.mitina.vaadin.app;

public class CounterService {

    private static int counter = 1;

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        CounterService.counter = counter;
    }

    public static void incCounter(){
        counter++;
    }
}
