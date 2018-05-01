package ru.mitina.vaadin.app;

public class Currency {

    private String name;

    private String value;

    private String buy;

    private String sell;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        double d = Double.parseDouble(value);
        String v = String.format("%.2f", d);
        return v;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }
}
