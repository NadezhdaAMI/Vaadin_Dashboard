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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.mitina.vaadin.app.mongodb.Counter;
import ru.mitina.vaadin.app.mongodb.CounterRepository;

import javax.servlet.http.HttpServletRequest;

@EnableAutoConfiguration
@SpringUI
@Theme("darktheme")
public class MainUI extends UI{

    private static final Logger logger = LogManager.getLogger(MainUI.class.getName());

    private static Counter counter;

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

        final VerticalLayout baseL = new VerticalLayout();
        setContent(baseL);
        baseL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        baseL.setPrimaryStyleName("styleBaseL");

        HorizontalLayout mainL = new HorizontalLayout();
        mainL.setPrimaryStyleName("styleMainL");
        mainL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        logger.info("Заполняем layout для погоды");
        VerticalLayout wL = new VerticalLayout();
        wL.setHeight("450px");
        wL.setPrimaryStyleName("styleWLayout");

        VerticalLayout wHeader = new VerticalLayout();
        wHeader.setPrimaryStyleName("styleWHeader");
        Label labelW = new Label("Прогноз погоды ");
        wHeader.addComponent(labelW);

        VerticalLayout wMainL = new VerticalLayout();
        wMainL.setMargin(false);

        Map<Integer, String> map = WeatherService.getMap();
        NativeSelect sample = new NativeSelect<>("", map.values());
        sample.setEmptySelectionAllowed(false);
        sample.setSelectedItem(map.get(1496747));
        WeatherService.setCityName(map.get(1496747));

        sample.addValueChangeListener(event -> {
            String cityName = String.valueOf(event.getValue());
            WeatherService.setCityName(cityName);
            wMainL.removeAllComponents();
            String cName = WeatherService.getCityName();
            WeatherService.buildUrl(cName, WeatherService.beginURL, WeatherService.endURL, WeatherService.getUrlTod());
            WeatherService.buildUrl(cName, WeatherService.beginURL2, WeatherService.endURL, WeatherService.getUrlTom());
            try {
                WeatherService.setJsonWeather();
                WeatherService.fillItems(wMainL);
            }
            catch (Exception er){
                er.printStackTrace();
                Label eLabel = new Label("Сервер временно недоступен!");
                eLabel.setStyleName("indentM");
                wMainL.addComponent(eLabel);
            }
        });
        wHeader.addComponent(sample);
        wL.addComponent(wHeader);

        logger.info("Заполняется layout сегодняшнего дня");
        HorizontalLayout todayL = new HorizontalLayout();
        todayL.setPrimaryStyleName("layoutDayItem");

        VerticalLayout todayLdate = new VerticalLayout();
        todayLdate.setMargin(false);
        todayLdate.setHeight("20px");
        DateFormat formatDay = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Date date = new Date();
        todayLdate.addComponent(new Label(formatDay.format(date)));
        Label text = new Label("сегодня");
        text.setStyleName("textSize");
        todayLdate.addComponent(text);
        todayL.addComponent(todayLdate);

        logger.info("Заполняется layout завтрашнего дня");
        HorizontalLayout tomL = new HorizontalLayout();
        tomL.setPrimaryStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        Label tomInfo = new Label(formatDay.format(dateT));
        tomL.addComponent(tomInfo);

        wMainL.addComponent(todayL);
        wMainL.addComponent(tomL);
        logger.info("Заполнены layout-ы каждого дня данными о погоде");

        wL.addComponent(wMainL);
        Button buttonW = new Button("Обновить");
        buttonW.addClickListener( e -> {
            wMainL.removeAllComponents();
            String cName = WeatherService.getCityName();
            WeatherService.buildUrl(cName, WeatherService.beginURL, WeatherService.endURL, WeatherService.getUrlTod());
            WeatherService.buildUrl(cName, WeatherService.beginURL2, WeatherService.endURL, WeatherService.getUrlTom());
            try {
                WeatherService.setJsonWeather();
                WeatherService.fillItems(wMainL);
            }
            catch (Exception er){
                er.printStackTrace();
                Label eLabel = new Label("Сервер временно недоступен!");
                eLabel.setStyleName("indentM");
                wMainL.addComponent(eLabel);
            }
        });

        wL.addComponent(buttonW);
        wL.setComponentAlignment(buttonW, Alignment.BOTTOM_CENTER);
        mainL.addComponent(wL);

        VerticalLayout v2 = new VerticalLayout();
        v2.setPrimaryStyleName("styleV2");
        logger.info("Заполняем layout для валюты");
        VerticalLayout mL = new VerticalLayout();
        mL.setPrimaryStyleName("styleMLayout");

        Label labelM = new Label("Курсы валют");
        mL.addComponent(labelM);
        Grid<Currency> grid = new Grid<>();
        grid.setWidth("430px");
        grid.setHeight("116px");

        Currency usd = new Currency();
        usd.setName("USD/RUB");
        usd.setSign("...");
        Currency eur = new Currency();
        eur.setName("EUR/RUB");
        eur.setSign("...");

        List<Currency> valute = Arrays.asList(usd, eur);
        grid.setItems(valute);
        logger.info("получен контент для grid");
        grid.addColumn(Currency::getName).setCaption("Валюта");
        grid.addColumn(Currency::getSign).setCaption("КУРС ЦБ");
        grid.addColumn(Currency::getSign).setCaption("ПОКУПКА");
        grid.addColumn(Currency::getSign).setCaption("ПРОДАЖА");
        logger.info("grid заполнена ...");

        VerticalLayout vl2 = new VerticalLayout();
        vl2.setMargin(false);
        mL.addComponent(vl2);
        vl2.addComponent(grid);
        mL.addComponent(vl2);
        Button buttonM = new Button("Обновить");
        buttonM.addClickListener( e -> {
            try {
                vl2.removeAllComponents();
                grid.removeAllColumns();
                CurrencyService.fillGrid(grid);
                vl2.addComponent(grid);
            } catch (Exception err){
                vl2.removeComponent(grid);
                err.printStackTrace();
                Label eLabel = new Label("Cервер временно недоступен!");
                eLabel.setStyleName("indent");
                vl2.addComponent(eLabel);
            }
        });

        mL.addComponent(buttonM);
        mL.setComponentAlignment(buttonM, Alignment.BOTTOM_CENTER);
        v2.addComponent(mL);

        Panel panelCounter = new Panel("Счетчик посещений");
        panelCounter.setPrimaryStyleName("panelCounter");
        panelCounter.setContent(new Label(String.valueOf(counter)));

        v2.addComponent(panelCounter);
        v2.setComponentAlignment(panelCounter, Alignment.BOTTOM_CENTER);
        mainL.addComponent(v2);
        mainL.setComponentAlignment(v2, Alignment.TOP_RIGHT);

        baseL.addComponent(mainL);

        HorizontalLayout footerL = new HorizontalLayout();
        footerL.setWidth("950px");
        footerL.setHeight("60px");
        footerL.setStyleName("styleFooter");

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date now = new Date();
        Label stateInfo = new Label("Информация по состоянию на " + dateFormat.format(now));

        logger.info("Получение IP адреса клиента...");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        Label ipInfo = new Label("Ваш IP-адрес: " + request.getRemoteAddr());

        footerL.addComponent(stateInfo);
        footerL.addComponent(ipInfo);
        footerL.setComponentAlignment(ipInfo, Alignment.TOP_RIGHT);
        baseL.addComponent(footerL);

        panelCounter.setContent(new Label(String.valueOf(counter)));
        logger.info("Добро пожаловать на сайт!");
    }
}
