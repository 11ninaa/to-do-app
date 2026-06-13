# To-Do App

Веб апликација за управување со задачи изградена со Spring Boot. Поддржува регистрација со потврда преку е-пошта, најава со двофакторска автентикација (2FA), управување со задачи и администраторски панел со контрола на пристап базирана на улоги.

---

## Технологии

- **Java 17**
- **Spring Boot 3.5** (Web, JPA, Thymeleaf, SSL)
- **H2** — вградена база на податоци (file mode)
- **JavaMail** — испраќање на верификациски кодови
- **MailHog** — локален SMTP сервер за развој

---

## Функционалности

### Автентикација
- Регистрација со верификација преку е-пошта (код важи 10 минути)
- Најава со двофакторска автентикација — при секоја најава се испраќа 6-цифрен код на е-пошта (важи 5 минути)
- Безбедно хаширање на лозинки со SHA-256 + индивидуален salt
- Барање за силна лозинка: минимум 8 знаци, голема буква, мала буква, цифра и специјален знак

### Задачи
- Додавање, прегледување и бришење на лични задачи
- Секој корисник гледа само свои задачи

### Контрола на пристап
- Организациски улоги: `GUEST`, `ADMIN`
- JIT (Just-in-Time) привремен пристап до ресурси со автоматско истекување
- Администраторски панел за преглед на сите корисници

---

## Поставување

### Барања

- Java 17+
- [MailHog](https://github.com/mailhog/MailHog) за локален SMTP (порт `1025`, UI на порт `8025`)
- SSL keystore (`.p12`)

### SSL Keystore

Генерирај keystore пред стартување:

```bash
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 365
```

Постави го во `src/main/resources/keystore.p12`.

### Конфигурација

Во `src/main/resources/application.properties` пополни:

```properties
spring.datasource.username=<корисник>
spring.datasource.password=<лозинка>
server.ssl.key-store-password=<keystore лозинка>
```

### Стартување

```bash
# 1. Стартувај MailHog
mailhog

# 2. Стартувај ја апликацијата
./mvnw spring-boot:run
```

Апликацијата се стартува на `https://localhost:8443`.  
MailHog UI за преглед на мејлови: `http://localhost:8025`.

---

## Структура на проектот

```
src/main/java/informaciska/com/ToDo/
├── AuthController.java         # Регистрација, верификација, најава, 2FA
├── AuthFilter.java             # Заштита на рути — проверка на сесија
├── AuthService.java            # Организациски улоги и JIT пристап
├── EmailService.java           # Испраќање на верификациски кодови
├── PasswordUtil.java           # Хаширање со SHA-256 + salt
├── TaskService.java            # Логика за задачи
├── UserService.java            # Регистрација и 2FA логика
└── UserResourceRole.java       # JIT привремен пристап до ресурси
```

---

## Безбедност

- Комуникацијата е исклучиво преку **HTTPS** (порт 8443)
- Лозинките се хешираат со **SHA-256 + random salt** — никогаш не се чуваат во plain text
- Сесиските колачиња се заштитени со `HttpOnly` и `SameSite=Strict`
