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


/** Класс для создания пользовательского интерфейса */
@EnableAutoConfiguration
@SpringUI
@Theme("darktheme")
public class MainUI extends UI{

    private static final Logger LOG = LogManager.getLogger(MainUI.class.getName());

    /** Id города Новосибирска */
    private static final int NSK_ID = 1496747;

    /** Счетчик посещений */
    private static Counter counter;

    @Autowired
    private CounterRepository repository;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        /* Увеличиваем счетчик при каждой загрузке страницы */
        incCount();

        /* Базовый layout */
        final VerticalLayout baseL = new VerticalLayout();
        setContent(baseL);
        baseL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        baseL.setPrimaryStyleName("styleBaseL");

        /* Основной layout, на котором размещаются layout-ы с погодой и валютой */
        HorizontalLayout mainL = new HorizontalLayout();
        mainL.setPrimaryStyleName("styleMainL");
        mainL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        LOG.info("Заполняем layout для погоды");

        /* wL - layout для погоды */
        VerticalLayout wL = new VerticalLayout();
        wL.setHeight("450px"); //при удалении этой строчки, sample исчезает
        wL.setPrimaryStyleName("styleWLayout");

        /* wHeader - для форматирования заголовка погоды */
        VerticalLayout wHeader = new VerticalLayout();
        wHeader.setPrimaryStyleName("styleWHeader");
        Label labelW = new Label("Прогноз погоды ");
        labelW.setStyleName("styleTitle");
        wHeader.addComponent(labelW);

        /* wMainL содержит два layouta(погода сегодня и завтра), создается заново при изменении города */
        VerticalLayout wMainL = new VerticalLayout();
        wMainL.setMargin(false);

        /* sample - селектор для выбора города, map - содержимое */
        Map<Integer, String> map = WeatherService.getMap();
        NativeSelect sample = new NativeSelect<>("", map.values());
        sample.setEmptySelectionAllowed(false);
        sample.setSelectedItem(map.get(NSK_ID));
        WeatherService.setCityName(map.get(NSK_ID));

        sample.addValueChangeListener(event -> {
            String cityName = String.valueOf(event.getValue());
            WeatherService.setCityName(cityName);
            wMainL.removeAllComponents();
            String cName = WeatherService.getCityName();
            WeatherService.buildUrl(cName, WeatherService.BEGIN_URL, WeatherService.END_URL, WeatherService.getUrlTod());
            WeatherService.buildUrl(cName, WeatherService.BEGIN_URL2, WeatherService.END_URL, WeatherService.getUrlTom());
            notifyClient(wMainL);
        });
        wHeader.addComponent(sample);
        wL.addComponent(wHeader);

        LOG.info("Заполняется layout сегодняшнего дня");
        /* todayL горизонтальный layout со всеми данными о погоде сегодня*/
        HorizontalLayout todayL = new HorizontalLayout();
        todayL.setPrimaryStyleName("layoutDayItem");

        VerticalLayout todayLdate = new VerticalLayout();
        todayLdate.setStyleName("todayLdate");
        DateFormat formatDay = new SimpleDateFormat("E', ' dd.MM ", new Locale("ru"));
        Date date = new Date();
        todayLdate.addComponent(new Label(formatDay.format(date)));
        Label text = new Label("сегодня");
        text.setStyleName("textSize");
        todayLdate.addComponent(text);
        todayL.addComponent(todayLdate);

        LOG.info("Заполняется layout завтрашнего дня");
        /* tomL горизонтальный layout со всеми данными о погоде завтра */
        HorizontalLayout tomL = new HorizontalLayout();
        tomL.setPrimaryStyleName("layoutDayItem");
        Date dateT = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        Label tomInfo = new Label(formatDay.format(dateT));
        tomL.addComponent(tomInfo);

        wMainL.addComponent(todayL);
        wMainL.addComponent(tomL);
        LOG.info("Заполнены layout-ы каждого дня данными о погоде");

        wL.addComponent(wMainL);
        Button buttonW = new Button("Обновить");
        buttonW.addClickListener( e -> {
            wMainL.removeAllComponents();
            String cName = WeatherService.getCityName();
            WeatherService.buildUrl(cName, WeatherService.BEGIN_URL, WeatherService.END_URL, WeatherService.getUrlTod());
            WeatherService.buildUrl(cName, WeatherService.BEGIN_URL2, WeatherService.END_URL, WeatherService.getUrlTom());
            notifyClient(wMainL);
        });

        wL.addComponent(buttonW);
        wL.setComponentAlignment(buttonW, Alignment.BOTTOM_CENTER);
        mainL.addComponent(wL);

        /* v2 - layout содержит layout-ы с валютой и счетчиком, нужен для выравнивания */
        VerticalLayout v2 = new VerticalLayout();
        v2.setPrimaryStyleName("styleV2");
        LOG.info("Заполняем layout для валюты");

        /* mL - layout для валюты */
        VerticalLayout mL = new VerticalLayout();
        mL.setPrimaryStyleName("styleMLayout");

        Label labelM = new Label("Курсы валют");
        labelM.setStyleName("styleTitle");
        mL.addComponent(labelM);
        Grid<Currency> grid = new Grid<>();
        grid.setWidth("430px"); // не подчиняется форматированию через setStyleName()
        grid.setHeight("116px");

        /* При стартовой загрузке страницы ячейки grid заполняются "..." */
        Currency usd = new Currency();
        usd.setName("USD/RUB");
        usd.setSign("...");
        Currency eur = new Currency();
        eur.setName("EUR/RUB");
        eur.setSign("...");

        List<Currency> valute = Arrays.asList(usd, eur);
        grid.setItems(valute);
        LOG.info("получен контент для grid");
        grid.addColumn(Currency::getName).setCaption("Валюта");
        grid.addColumn(Currency::getSign).setCaption("КУРС ЦБ");
        grid.addColumn(Currency::getSign).setCaption("ПОКУПКА");
        grid.addColumn(Currency::getSign).setCaption("ПРОДАЖА");
        LOG.info("grid заполнена ...");

        /* vl2 layout нужен для выравнивания и обновления grid */
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
                Label eLabel = new Label("Сервер временно недоступен!");
                eLabel.setStyleName("indent");
                vl2.addComponent(eLabel);
            }
        });

        mL.addComponent(buttonM);
        mL.setComponentAlignment(buttonM, Alignment.BOTTOM_CENTER);
        v2.addComponent(mL);

        /* panelCounter панель для отображения количества посещений*/
        Panel panelCounter = new Panel("Счетчик посещений");
        panelCounter.setPrimaryStyleName("panelCounter");
        panelCounter.setContent(new Label(String.valueOf(counter)));

        v2.addComponent(panelCounter);
        v2.setComponentAlignment(panelCounter, Alignment.BOTTOM_CENTER);
        mainL.addComponent(v2);
        mainL.setComponentAlignment(v2, Alignment.TOP_RIGHT);

        baseL.addComponent(mainL);

        /* footerL - layout содержит label-ы с ip и текущим временем */
        HorizontalLayout footerL = new HorizontalLayout();
        footerL.setStyleName("styleFooter");

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date now = new Date();
        Label stateInfo = new Label("Информация по состоянию на " + dateFormat.format(now));

        LOG.info("Получение IP адреса клиента...");
        Label ipInfo = new Label("Ваш IP-адрес: ");
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            ipInfo = new Label("Ваш IP-адрес: " + request.getRemoteAddr());
        } catch (IllegalStateException e){
            LOG.error("Ваш IP-адрес не удалось установить");
        }
        ipInfo.setStyleName("ipInfo");

        footerL.addComponent(stateInfo);
        footerL.addComponent(ipInfo);
        footerL.setComponentAlignment(ipInfo, Alignment.TOP_RIGHT);
        baseL.addComponent(footerL);

        panelCounter.setContent(new Label(String.valueOf(counter)));

        LOG.info("Добро пожаловать на сайт!");
    }

    /** Увеличиваем счетчик посещений на 1 при каждой загрузке страницы */
    private void incCount(){
        try {
            if (repository.findAll().isEmpty()) {
                counter = new Counter(0);
                repository.save(counter);
            }
            LOG.info("до - " + repository.findAll().get(0));
            int n = repository.findAll().get(0).incCounter();
            LOG.info("после - " + n);
            repository.deleteAll();
            counter = new Counter(n);
            repository.save(counter);
            LOG.info("Счетчик посещений увеличен на 1");

        } catch (NullPointerException ex){
            ex.printStackTrace();
            LOG.error("Произошла ошибка при обращении к базе!");
        }
    }

    /** Выводится сообщение клиенту при недоступности сервера
     * @param wMainL layout, в который выводится сообщение об ошибке
     */
    private void notifyClient(VerticalLayout wMainL){
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
    }
}
