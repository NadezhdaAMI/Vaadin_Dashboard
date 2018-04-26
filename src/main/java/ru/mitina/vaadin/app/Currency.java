package ru.mitina.vaadin.app;

public class Currency {

    private String name;

    private double value;

    private double previous;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {

        this.value = value;
        String formattedDouble = String.format("%.2f", this.value);
        this.value = Double.parseDouble(formattedDouble);
    }

    public double getPrevious() {
        return previous;
    }

    public void setPrevious(double previous) {

        this.previous = previous;
        String formattedDouble = String.format("%.2f", this.previous);
        this.previous = Double.parseDouble(formattedDouble);
    }
}
