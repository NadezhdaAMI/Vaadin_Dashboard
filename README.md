## Информер прогноза погоды и валют

![Интерфейс приложения](https://github.com/NadezhdaAMI/LeetCode/blob/master/Content/screen.gif)

####     Данное приложение позволяет получить следующую информацию: 
* основные параметры погоды в трех городах (Новосибирск, Москва, Санкт-Петербург) в настоящий момент и на завтра, 
* текущие курсы валют от Центробанка РФ и стоимость валют при покупке и продаже в Сбербанке,
* IP-адрес клиента

Проект написан с использованием фреймворка Vaadin.

#####     Автор: Надежда A. М.


Необходимые инструменты для запуска приложения
----------------------------------------------

1. Java Development Kit (JDK) 8
2. Maven 3
3. Установленные пакеты mongo-сервера


Установка и настройка
---------------------

Клонировать репозиторий: 
$ git clone https://github.com/NadezhdaAMI/Vaadin_Dashboard

Выполнить в командной строке:

1. mvn package 
2. mvn tomcat:run
3. открыть http://localhost:8080 в веб браузере.


Документация
------------

Данное приложение полностью документировано, javadoc можно найти через
app/src/main/java/javadoc


Логи
----

Для логгирования использовался log4j2,
c сохранением результатов запуска в папку
app/logs


Контакты
--------

e-mail: nadezhda.a.mit@gmail.com
