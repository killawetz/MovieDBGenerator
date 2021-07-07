# Генерация тестовых данных, Лабораторная №2 из курса "Базы данных" #

## Цели работы ##
Сформировать набор данных, позволяющий производить операции на реальных объемах данных.


Структура БД:
![schema](MovieDBGenerator/images/MovieLibDB.png)


## Программа работы ##

1. Реализация в виде программы параметризуемого генератора, который позволит сформировать набор связанных данных в каждой таблице.
2. Частные требования к генератору, набору данных и результирующему набору данных:

* количество записей в справочных таблицах должно соответствовать ограничениям предметной области
* количество записей в таблицах, хранящих информацию об объектах или субъектах должно быть параметром генерации
* значения для внешних ключей необходимо брать из связанных таблиц
* сохранение уже имеющихся данных в базе данных

## Ход работы ##

### Выбор языка программирования ##

Для написания генераторов был выбран язык java с драйвером jdbc для взаимодействия с СУБД. СУБД как и в лабораторной работе №1 остается postgresql

### Способ генерации данных ###

Так как методическое описание к лабораторной работе дает свободу в выборе варианта генерации данных мной был выбран способ адаптации готовых данных с "заплатками" в виде генераторов в тех местах, в которых выбранный мной ресурс с данными не мог покрыть.  
Данные для таких сущностей как: Film, Person, Country, Genre, Studio, Role и Screenshot были взяты с ресурса [https://www.themoviedb.org/](themoviedb.org) который является большой базой данных фильмов с удобным API. Для взаимодействия с сетью использовалась библиотека retrofit.  
Получить данные для сущности Award с themoviedb оказалось невозможным, поэтому мной в ручную был составлен локальный датасет в виде json файла: [award.json](MovieDBGenerator/src/main/data/awards.json)

### Дополненительная информация ###

Так же, стоит упомянуть, что в лабораторной №2 я забыл добавить ограничения уникальности для всех таблиц, что в свою очередь является немаловажным аспектом при генерации.  
SQL-командами приведенными ниже были добавлены ограничения уникальности для каждой таблицы.  
```sql
ALTER TABLE genre ADD UNIQUE (genre_name);
ALTER TABLE country ADD UNIQUE (country_name);
ALTER TABLE screenshot ADD UNIQUE (url);
ALTER TABLE studio ADD UNIQUE (studio_name);
ALTER TABLE role ADD UNIQUE (position);
ALTER TABLE award ADD UNIQUE (award_name);
ALTER TABLE person ADD UNIQUE (name, date_of_birth);
ALTER TABLE genre_film ADD UNIQUE (genre_id, film_id);
ALTER TABLE country_film ADD UNIQUE (country_id, film_id);
ALTER TABLE person_award ADD UNIQUE (person_in_film_id, award_id);
ALTER TABLE person_in_film ADD UNIQUE (person_id, role_id, film_id);
ALTER TABLE film ADD UNIQUE (name, year);
```

### Генерация данных для таблицы Film ###
Данные для всех столбцов таблицы получались GET-запросом от themoviedb, однако их база данных не располагает информацией для каждого предоставляемого ими фильма в полном объеме.
Поэтому для **budget** в тех случаях, когда получить информацию для этого поля не получалось генерировалось случайное число в диапазоне от 1 до Integer.MAX_VALUE.
Идентичная ситуация со столбцом **description** (хотя встречается значительно реже). Для решения данной проблемы было взято [пользовательское решение с гитхаба](https://github.com/mdeanda/lorem) - генератор Lorem Ipsum.  
Код модели данных - [FilmModel.java](MovieDBGenerator/src/main/java/Model/FilmModel.java)  
Код класса генератора - [FilmGenerator.java](MovieDBGenerator/src/main/java/Generator/FilmGenerator.java)  

### Генерация данных для таблицы Country ###
В данном случае themoviedb прекрасно удовлетворяет все наши потребности - возвращается список из большого количества стран.  
Код модели данных - [CountryModel.java](MovieDBGenerator/src/main/java/Model/CountryModel.java)  
Код класса генератора - [CountryGenerator.java](MovieDBGenerator/src/main/java/Generator/CountryGenerator.java)  

### Генерация данных для таблицы Genre ###
Датасет themoviedb содержит в себе *19* наименований различных жанров кино, которые по моему мнению удачно соответствуют ограничениям предметной области и дают возможность сгенерировать большое количество комбинаций для таблицы genre_film. Вследствие чего отказался от идеи генерации каких-либо дополнительных "жанров".  
Код модели данных - [GenreModel.java](MovieDBGenerator/src/main/java/Model/GenreModel.java)  
Код класса генератора - [GenreGenerator.java](MovieDBGenerator/src/main/java/Generator/GenreGenerator.java)  

### Генерация данных для таблицы Studio ###
Themoviedb содержит в себе огромное количество наименований различных студий и компаний кинопроизводства.  
Код модели данных - [StudioModel.java](MovieDBGenerator/src/main/java/Model/StudioModel.java)  
Код класса генератора - [StudioGenerator.java](MovieDBGenerator/src/main/java/Generator/StudioGenerator.java)  

### Генерация данных для таблицы Person ###
Для таблицы *Person* из themoviedb я получал имена людей(**person_name**) и дату рождения(**date_of_birth**), однако не для каждого человека предоставляется дата рождения, в таком случае дата рождения генерировалась.  
Поле *country_of_birth* заполнялось рандомным id полученным в результате sql-запроса ```sql SELECT id FROM country ORDER BY random() LIMIT 1 ```.  
Код модели данных - [PersonModel.java](MovieDBGenerator/src/main/java/Model/PersonModel.java)  
Код класса генератора - [PersonGenerator.java](MovieDBGenerator/src/main/java/Generator/PersonGenerator.java)  

### Генерация данных для таблицы Screen ###
URL адреса изображения получались GET запросом к themoviedb. Поле **film_id** заполнялось рандомным id полученным в результате sql-запроса ```sql SELECT id FROM film ORDER BY random() LIMIT 1 ```.  
Код модели данных - [FilmModel.java](MovieDBGenerator/src/main/java/Model/FilmModel.java)  
Код класса генератора - [ScreenshotGenerator.java](MovieDBGenerator/src/main/java/Generator/ScreenshotGenerator.java)  

### Генерация данных для таблицы Role ###
Датасет themoviedb содержит в себе *12* наименований различных подразделений работников на съемочной площадке, которые по моему мнению удачно соответствуют ограничениям предметной области и дают возможность сгенерировать большое количество комбинаций для таблицы person_in_film. Вследствие чего отказался от идеи генерации множества различных должностей.  
Код модели данных - [RoleModel.java](MovieDBGenerator/src/main/java/Model/RoleModel.java)  
Код класса генератора - [RoleGenerator.java](MovieDBGenerator/src/main/java/Generator/RoleGenerator.java)  

### Генерация данных для таблицы Award ###
Как уже упоминалось выше themoviedb не продоставляет возможности получить какую-нибудь информацию в категории "награды". Найти альтернативный сервис с удобным API и хорошим датасетом мне не удалось, поэтому я решил создать свой локальный датасет внутри проекта.  
В нем содержится *20* различных наименований наград и премий. Я счел это достаточным числом, которое дает возможность сгенерировать большое количество комбинаций для таблицы person_award.  
Код модели данных - [AwardModel.java](MovieDBGenerator/src/main/java/Model/AwardModel.java)  
Код класса генератора - [AwardGenerator.java](MovieDBGenerator/src/main/java/Generator/AwardGenerator.java)  

### Дополнительная информация ###
Хотя таблицы *Genre*, *Role* и *Award* имеют ограниченный пул значений, генераторы для этих данных все равно являются параметризированными (требования ТЗ).  

### Генерация данных для ассоциативных таблиц ###
Генерируются данные для таблиц *genre_film*, *country_film*, *studio_film*, *person_in_film*, *person_award*.  
Код класса генератора - [GenerateAssociativeEntities.java](MovieDBGenerator/src/main/java/Generator/GenerateAssociativeEntities.java)

### Результаты работы генераторов ##

Генерация 15 значений для таблицы *film*:  

![generate film](MovieDBGenerator/images/FilmGenerate.png)

Генерация 10 значений для таблицы *studio*:  

![generate studio](MovieDBGenerator/images/StudioGenerate.png)

Генерация 13 значений для таблицы *genre*:  

![generate genre](MovieDBGenerator/images/GenreGenerate.png)

Генерация 20 значений для таблицы *country*:  

![generate country](MovieDBGenerator/images/CountryGenerate.png)

Генерация 30 значений для таблицы *person*:  

![generate person](MovieDBGenerator/images/PersonGenerate.png)

Генерация 20 значений для ассоциативной таблицы *genre_film*:  

![generate genre_film](MovieDBGenerator/images/GenreFilmGenerate.png)

Генерация 30 значений для ассоциативной таблицы *person_in_film*:  

![generate person_in_film](MovieDBGenerator/images/PersonInFilmGenerate.png)

### Вывод ###

Результатом выполнения данной лабораторной работы является набор полноценных параметризированных генераторов для наполнения таблиц в нашей базе данных. Получены практические навыки взаимодействия с СУБД постредством jdbc, взаимодействия с сетью посредством бибилотеки retrofit и навыки сериализации/диссериализации JSON.



