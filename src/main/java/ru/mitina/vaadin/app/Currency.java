package ru.mitina.vaadin.app;

public class Currency {

    private String name;

    private String value;

    private String buy;

    private String sell;

    private String sign;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        double d = Double.parseDouble(value);
        return String.format("%.2f", d);
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}

