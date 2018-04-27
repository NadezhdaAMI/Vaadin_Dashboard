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

        Label count = new Label(String.valueOf(CounterService.getCounter()));
        Panel panelCounter = new Panel("Счетчик посещений");
        panelCounter.setWidth("160px");
        panelCounter.setHeight("80px");
        panelCounter.setContent(count);

        //Weather panel
        Panel panelW = new Panel("Прогноз погоды ");

        // Create the content
        FormLayout content = new FormLayout();
        content.setMargin(false);
        VerticalLayout dayitem = new VerticalLayout();

        dayitem.setStyleName("backColorGreen");
        dayitem.setMargin(false);

        Map<Integer, String> map = WeatherService.getMap();

        NativeSelect sample = new NativeSelect<>("", map.values());

        sample.setEmptySelectionAllowed(false);
        sample.setSelectedItem(map.get(1496747));

        sample.addValueChangeListener(event -> {
                    String cityName = String.valueOf(event.getValue());
                    WeatherService.buildUrl(cityName);
                });

        dayitem.addComponent(sample);

        VerticalLayout itemsL = new VerticalLayout();
        itemsL.setMargin(false);
        dayitem.addComponent(itemsL);

        WeatherService.fillItems(itemsL);

        Button buttonW = new Button("Обновить");

        buttonW.addClickListener( e -> {
            itemsL.removeAllComponents();
            WeatherService.setJsonWeather();
            WeatherService.fillItems(itemsL);
            CounterService.incCounter();
            panelCounter.setContent(new Label(String.valueOf(CounterService.getCounter())));
        });

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

        Grid<Currency> grid = new Grid<>();
        grid.setWidth("270px");
        grid.setHeight("120px") ;
        CurrencyService.fillGrid(grid);
        vertLayout.addComponent(grid);

        Button buttonM = new Button("Обновить");

        buttonM.addClickListener( e -> {
            grid.removeAllColumns();
            CurrencyService.fillGrid(grid);
            CounterService.incCounter();
            panelCounter.setContent(new Label(String.valueOf(CounterService.getCounter())));
        });

        vertLayout.addComponent(buttonM);
        vertLayout.setComponentAlignment(buttonM, Alignment.MIDDLE_CENTER);

        contentM.addComponent(vertLayout);
        panelM.setContent(contentM);


        layoutAb.addComponent(panelCounter, "right: 10px; top: 150px;");

        layoutHor.addComponent(panelW);
        layoutHor.addComponent(panelM);
        layoutHor.setComponentAlignment(panelW, Alignment.MIDDLE_CENTER);
        layoutHor.setComponentAlignment(panelM, Alignment.MIDDLE_CENTER);

        HorizontalLayout layoutLang = new HorizontalLayout();
        Button button1 = new Button("Rus");
        Button button2 = new Button("Eng");
        layoutLang.addComponent(button1);
        layoutLang.addComponent(button2);
        layoutAb.addComponent(layoutLang, "right: 10px; top: 10px;");

        layoutAb.addComponent(layoutHor);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        Date date = new Date();
        Label stateInfo = new Label("Информация по состоянию на " + dateFormat.format(date));
        Label ipInfo = new Label("Ваш IP-адрес: " + GetCurrentIP.getIpAddress());

        layoutAb.addComponent(stateInfo, "left: 10px; bottom: 0px;");
        layoutAb.addComponent(ipInfo, "right: 10px; bottom: 0px;");
        setContent(layout);
    }
}
