package ru.mitina.vaadin.app;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import ru.mitina.vaadin.app.mongodb.Counter;
import ru.mitina.vaadin.app.mongodb.CounterRepository;

@EnableAutoConfiguration
@SpringUI
@Theme("darktheme")
public class MainUI extends UI{

    static final Logger logger = LogManager.getLogger(MainUI.class.getName());

    public static Counter counter;

    @Autowired
    private CounterRepository repository;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        try {
            logger.info("до - " + repository.findAll().get(0));
            int n = repository.findAll().get(0).incCounter();
            logger.info("после - " + n);
            repository.deleteAll();
            counter = new Counter(n);
            repository.save(counter);
            logger.info("Счетчик посещений увеличен на 1");

        } catch (NullPointerException ex){
            ex.printStackTrace();
            logger.error("Произошла ошибка при обращении к базе!");
        }

        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        layout.setMargin(false);
        layout.setStyleName("backColorFon");

        HorizontalLayout h2 = new HorizontalLayout();
        h2.setWidth("970px");
        h2.setHeight("550px");
        h2.setMargin(false);
        h2.setStyleName("backColorBlue");
        h2.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        logger.info("Заполняем layout для погоды");
        VerticalLayout dayitem = new VerticalLayout();
        dayitem.setWidth("320px");
        dayitem.setHeight("450px");
        dayitem.setStyleName("backColorGreen");
        dayitem.setMargin(true);

        VerticalLayout dayitemF = new VerticalLayout();
        dayitemF.setHeight("30px");
        dayitemF.setStyleName("backColorFon2");
        dayitemF.setMargin(false);
        Label labelW = new Label("Прогноз погоды ");
        dayitemF.addComponent(labelW);

        VerticalLayout itemsL = new VerticalLayout();
        itemsL.setStyleName("backColorYellow");
        itemsL.setMargin(false);
        WeatherService.fillItems(itemsL);

        Map<Integer, String> map = WeatherService.getMap();

        NativeSelect sample = new NativeSelect<>("", map.values());

        sample.setEmptySelectionAllowed(false);
        sample.setSelectedItem(map.get(1496747));

        sample.addValueChangeListener(event -> {
            String cityName = String.valueOf(event.getValue());
            WeatherService.buildUrl(cityName, WeatherService.beginURL, WeatherService.endURL, WeatherService.getUrlTod());
            WeatherService.buildUrl(cityName, WeatherService.beginURL2, WeatherService.endURL, WeatherService.getUrlTom());
            itemsL.removeAllComponents();
            WeatherService.setJsonWeather();
            WeatherService.fillItems(itemsL);
        });

        dayitemF.addComponent(sample);
        dayitem.addComponent(dayitemF);
        dayitem.addComponent(itemsL);
        Button buttonW = new Button("Обновить");
        buttonW.addClickListener( e -> {
            itemsL.removeAllComponents();
            WeatherService.setJsonWeather();
            WeatherService.fillItems(itemsL);
        });

        dayitem.addComponent(buttonW);
        dayitem.setComponentAlignment(buttonW, Alignment.BOTTOM_CENTER);

        h2.addComponent(dayitem);

        logger.info("Заполняем layout для валюты");
        VerticalLayout vertLayout = new VerticalLayout();
        vertLayout.setWidth("320px");
        vertLayout.setHeight("450px");
        vertLayout.setStyleName("backColorGreen");
        vertLayout.setMargin(true);

        Label labelM = new Label("Курсы валют");
        vertLayout.addComponent(labelM);
        Grid<Currency> grid = new Grid<>();
        grid.setWidth("270px");
        grid.setHeight("115px");
        CurrencyService.fillGrid(grid);
        vertLayout.addComponent(grid);
        vertLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

        Button buttonM = new Button("Обновить");

        buttonM.addClickListener( e -> {
            grid.removeAllColumns();
            CurrencyService.fillGrid(grid);
        });

        vertLayout.addComponent(buttonM);
        vertLayout.setComponentAlignment(buttonM, Alignment.BOTTOM_CENTER);

        Panel panelCounter = new Panel("Счетчик посещений");
        panelCounter.setStyleName("panelCounter");
        panelCounter.setWidth("180px");
        panelCounter.setContent(new Label(String.valueOf(counter)));

        h2.addComponent(vertLayout);
        h2.addComponent(panelCounter);

        layout.addComponent(h2);

        HorizontalLayout h3 = new HorizontalLayout();
        h3.setWidth("950px");
        h3.setHeight("60px");
        h3.setStyleName("backColorFooter");

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        Label stateInfo = new Label("Информация по состоянию на " + dateFormat.format(date));
        Label ipInfo = new Label("Ваш IP-адрес: " + GetCurrentIP.getClientIp());

        h3.addComponent(stateInfo);
        h3.addComponent(ipInfo);
        h3.setComponentAlignment(ipInfo, Alignment.TOP_RIGHT);
        layout.addComponent(h3);

        panelCounter.setContent(new Label(String.valueOf(counter)));
        logger.info("Добро пожаловать на сайт!");
    }
}
