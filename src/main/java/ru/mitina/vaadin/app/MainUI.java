package ru.mitina.vaadin.app;

import com.jayway.jsonpath.JsonPath;
import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@SpringUI
@Theme("darktheme")
public class MainUI extends UI {

    static final Logger logger = LogManager.getLogger(MainUI.class.getName());

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        AbsoluteLayout layoutAb = new AbsoluteLayout();
        layoutAb.setWidth("900px");
        layoutAb.setHeight("500px");
        layoutAb.setStyleName("backColorGray");
        layout.addComponent(layoutAb);
        layout.setComponentAlignment(layoutAb, Alignment.MIDDLE_CENTER);

        HorizontalLayout layoutHor = new HorizontalLayout();

        //Weather panel

        // Content for the PopupView
        VerticalLayout popupContent = new VerticalLayout();
        popupContent.addComponent(new Label("Санкт-Петербург"));
        popupContent.addComponent(new Label("Москва"));
        popupContent.addComponent(new Label("Новосибирск"));
        popupContent.addComponent(new Label("Барнаул"));

        PopupView popup = new PopupView("Выберите город", popupContent);

        Panel panelW = new Panel("Прогноз погоды ");

        // Create the content
        FormLayout content = new FormLayout();
        VerticalLayout dayitem = new VerticalLayout();

        dayitem.setStyleName("backColorGreen");
        dayitem.setMargin(false);

        //today item
        HorizontalLayout dayToday = new HorizontalLayout();
        DateFormat dateFormatday = new SimpleDateFormat("E', ' dd.MM ");
        Date date = new Date();
        Label todayInfo = new Label(dateFormatday.format(date)); // today
        dayToday.addComponent(todayInfo);

        //tomorrow item
        HorizontalLayout dayTomorrow = new HorizontalLayout();
        dayToday.setStyleName("layoutDayItem");
        dayTomorrow.setStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        Label tomorrowInfo = new Label(dateFormatday.format(dateT)); // tomorrow
        dayTomorrow.addComponent(tomorrowInfo);

        dayitem.addComponent(popup);
        dayitem.addComponent(dayToday);
        dayitem.addComponent(dayTomorrow);

        Button buttonW = new Button("Обновить", VaadinIcons.REFRESH);
        dayitem.addComponent(buttonW);

        dayitem.setComponentAlignment(buttonW, Alignment.MIDDLE_CENTER);

        content.addComponent(dayitem);
        panelW.setContent(content);


        //Money panel
        Panel panelM = new Panel("Курсы валют");
        // Create the content
        FormLayout contentM = new FormLayout();
        VerticalLayout vertLayout = new VerticalLayout();
        vertLayout.setStyleName("backColorGreen");
        vertLayout.setMargin(false);

        String jsonMoney = CurrencyService.getStringJson();

        Currency usd = new Currency();
        usd.setName(JsonPath.read(jsonMoney, "$.Valute.USD.CharCode"));
        usd.setValue(JsonPath.read(jsonMoney, "$.Valute.USD.Value"));
        usd.setPrevious(JsonPath.read(jsonMoney, "$.Valute.USD.Previous"));

        Currency eur = new Currency();
        eur.setName(JsonPath.read(jsonMoney, "$.Valute.EUR.CharCode"));
        eur.setValue(JsonPath.read(jsonMoney, "$.Valute.EUR.Value"));
        eur.setPrevious(JsonPath.read(jsonMoney, "$.Valute.EUR.Previous"));


        // Have some data
        List<Currency> valute = Arrays.asList(usd, eur);

        // Create a grid bound to the list
        Grid<Currency> grid = new Grid<>();
        grid.setWidth("260px");
        grid.setHeight("150px") ;
        grid.setItems(valute);
        grid.addColumn(Currency::getName).setCaption("Валюта");
        grid.addColumn(Currency::getValue).setCaption("Сегодня");
        grid.addColumn(Currency::getPrevious).setCaption("Вчера");

        vertLayout.addComponent(grid);

        Button buttonM = new Button("Обновить");
        vertLayout.addComponent(buttonM);
        vertLayout.setComponentAlignment(buttonM, Alignment.MIDDLE_CENTER);

        contentM.addComponent(vertLayout);
        panelM.setContent(contentM);


        Panel panelCounter = new Panel("Счетчик посещений");
        panelCounter.setWidth("160px");
        panelCounter.setHeight("80px");
        Label count = new Label("192");
        panelCounter.setContent(count);
        layoutAb.addComponent(panelCounter, "right: 10px; top: 150px;");

        layoutHor.addComponent(panelW);
        layoutHor.addComponent(panelM);
        layoutHor.setComponentAlignment(panelW, Alignment.MIDDLE_CENTER);
        layoutHor.setComponentAlignment(panelM, Alignment.MIDDLE_CENTER);

        HorizontalLayout layoutlangVersion = new HorizontalLayout();
        Button button1 = new Button("Rus");
        Button button2 = new Button("Eng");
        layoutlangVersion.addComponent(button1);
        layoutlangVersion.addComponent(button2);
        layoutAb.addComponent(layoutlangVersion, "right: 10px; top: 10px;");

        layoutAb.addComponent(layoutHor);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        Label stateInfo = new Label("Информация по состоянию на " + dateFormat.format(date));
        Label ipInfo = new Label("Ваш IP-адрес: " + AppApplication.ip.getHostAddress());

        layoutAb.addComponent(stateInfo, "left: 10px; bottom: 0px;");
        layoutAb.addComponent(ipInfo, "right: 10px; bottom: 0px;");
        setContent(layout);
    }
}
