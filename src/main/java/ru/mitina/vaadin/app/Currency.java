package ru.mitina.vaadin.app;

/**
 * Класс для описания значений валюты
 *
 * */
public class Currency {

    /**  Название валюты*/
    private String name;

    /** Текущее значение валюты по курсу Центробанка РФ */
    private String value;

    /** Стоимость при покупке валюты в Сбербанке */
    private String buy;

    /** Стоимость при продаже валюты в Сбербанке */
    private String sell;

    /** Значение всех полей, когда сервер информера недоступен*/
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

