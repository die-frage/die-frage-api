# Проект "Die Frage"

## 1. Архитектура проекта

Проект "Die Frage" разработан с использованием следующих технологий и подходов:
- Spring Boot - фреймворк для создания Java приложений
- Spring Cloud - набор инструментов для построения микросервисных архитектур
- Микросервисная архитектура используется для улучшения масштабируемости и гибкости приложения

## 2. Структура проекта

Проект состоит из 8 приложений, каждое из которых выполняет определенную функцию:

1. **config-server** - предназначен для управления конфигурациями в микросервисной архитектуре.  
2. **discovery** - service discovery (обнаружение сервисов) для микросервисов.  
3. **gateway** - шлюз API упрощающий связь между клиентом и сервером.  
4. **auth-server** - для создания и проверки JWT-токенов используется централизованный сервер аутентификации и авторизации.  
5. **answer** - Представляет собой бизнес приложение для ответов студентов.  
6. **professor** - Представляет собой бизнес приложение для преподавателя.  
7. **student** - Представляет собой бизнес приложение для студентов.  
8. **survey** - Представляет собой бизнес приложение для создания опросов.  

Модуль **exceptions** - реализован в качестве библиотеки, который подключается через зависимости в остальные модули.


Схема архитектуры приложения:  
<img src="images/cloud.png" alt="Архитектура" width="700"/>  


Порты для доступа к приложениям:
- 8888: [config-server](http://localhost:8888)
- 8761: [discovery](http://localhost:8761)
- 8787: [gateway](http://localhost:8787)
- 8010: [auth-server](http://localhost:8010)
- 8020: [student](http://localhost:8020)
- 8030: [professor](http://localhost:8030)
- 8040: [survey](http://localhost:8040)
- 8050: [answer](http://localhost:8050)

## 3. База данных

Проект использует PostgreSQL. Подключение осуществляется по адресу `jdbc:postgresql://localhost:5432`.

Названия баз данных, используемых микросервисами:
- answers
- students
- credentials
- surveys

Для корректной работы микросервисов требуется настройка конфигурационных файлов в папке `resources/configurations`. Важно, чтобы название файла совпадало с названием модуля микросервиса. Например, конфигурационный файл для микросервиса student может иметь следующий вид:

### student.yml:

```yaml
eureka:
  instance:
    hostname: localhost
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

server:
  port: 8020
spring:
  application:
    name: student

datasource:
  driver-class-name: org.postgresql.Driver
  url: jdbc:postgresql://localhost:5432/students
  username: postgres
  password: postgres
  jpa:
    database: postgresql
    hibernate:
      ddl-auto: update
    show-sql: true
  properties:
    hibernate:
      dialect: org.hibernate.dialect.PostgreSQLDialect
```
## 4. Запуск проекта 

Проект собирается с помощью команды из корневой директории проекта:

```bash
mvn clean install
```

Пример запуска микросервиса:  
```bash
java -jar die-frage-api/config-server/target/*.jar
```


## 5. OPEN API

Swagger используется для документирования API и упрощения тестирования запросов. Спецификации для endpoints находятся в следующих директориях:

- **[student](student/src/main/resources/specification.yml)**: `student/src/main/resources/`
- **[professor](professor/src/main/resources/specification-prof.yaml)**: `professor/src/main/resources/`
- **[survey](survey/src/main/resources/specification-survey.yaml)**: `survey/src/main/resources/`
- **[auth-server](auth-server/src/main/resources/specification.yml)**: `auth-server/src/main/resources/`

Пример:  

<img src="images/api.png" width="700" alt="Auth api"/>

## 6. Используемые технологии
Проект "Die Frage" разработан с использованием следующих технологий:
<img src="https://github.com/tomchen/stack-icons/blob/master/logos/spring.svg" alt="Spring" width="120"/>  
<img src="https://github.com/tomchen/stack-icons/blob/master/logos/java.svg" alt="Java" width="120"/>  
<img src="https://github.com/tomchen/stack-icons/blob/master/logos/postgresql-3.svg" alt="PostgreSQL" width="120"/>   
<img src="https://github.com/tomchen/stack-icons/blob/master/logos/swagger-5.svg" alt="Swagger" width="120"/>  

