

# 📘 Spring Core: Поглиблене Занурення

---

# 📘 Spring Core: Вступ

Сьогодні ми зануримося у світ **Spring Core**, зосереджуючись на **анотаціях**. Ми побачимо, як вони спрощують розробку, автоматизуючи рутинні завдання, та як корелюють з принципами IoC/DI. Наша мета — зрозуміти "магію" Spring під капотом.

---

# 📌 Завдання лекції

* Зрозуміти роль та використання власних анотацій: `@MyComponent`, `@MyAutowired`, `@MyScope`.
* Дослідити спрощену реалізацію IoC-контейнера `MyApplicationContext`.
* Простежити життєвий цикл бінів та механізми впровадження залежностей.
* Зміцнити розуміння принципів Dependency Injection (DI) та Inversion of Control (IoC).
* Провести паралелі з реальним Spring Framework та показати застосування у проєктах.

---

# 🧠 `MyApplicationContext` – Наш IoC-Контейнер

`MyApplicationContext` є серцем нашого спрощеного Spring-подібного контейнера. Його основна відповідальність — сканування пакетів, створення бінів та автоматичне впровадження залежностей.

---

# ⚙️ Сканування пакетів

Контейнер починає роботу зі сканування пакетів, шукаючи класи з `@MyComponent`.

**Порівняння з реальним Spring:**
Це аналог `@ComponentScan` у Spring. У реальному Spring ви часто бачите `@SpringBootApplication` (включає `@ComponentScan`) або `scanBasePackages` у конфігурації.

---

# 💡 Приклад сканування з життя

Уявіть, що ви менеджер на великому заводі.

`@ComponentScan` — це ваш відділ кадрів, який сканує всі резюме (класи) і шукає співробітників (біни) з потрібними кваліфікаціями (анотаціями).

---

# ⚙️ Створення Бінів

Для кожного знайденого компонента створюється екземпляр класу.
Якщо бін `SINGLETON` (за замовчуванням або `@MyScope(Scope.SINGLETON)`), він кешується та перевикористовується.
Для `PROTOTYPE` бінів створюється новий екземпляр при кожному запиті.

**Порівняння з реальним Spring:**
Це базовий механізм створення бінів. Spring також використовує рефлексію для інстанціювання класів.

---

# 💡 Приклад створення бінів з життя

Якщо ви замовляєте каву в кав'ярні:

* "Singleton" — це великий кавовий апарат, який використовується для всіх замовлень. Він один.
* "Prototype" — це одноразовий стаканчик, який створюється для кожного нового замовлення. Кожен раз новий.

---

# ⚙️ Впровадження Залежностей (DI)

Після створення `SINGLETON`-бінів, контейнер проходить по їхніх полях, шукаючи `@MyAutowired`. Якщо поле знайдено, він впроваджує відповідний бін з контексту.

**Порівняння з реальним Spring:**
Це серце Dependency Injection (DI) у Spring. Spring використовує різні стратегії впровадження (поле, конструктор, сеттер), але `@Autowired` найпоширеніший.

---

# 💡 Приклад DI з життя

Уявіть, що ви збираєте меблі з IKEA.

Замість того, щоб шукати кожну деталь (залежність) самостійно, IKEA (наш контейнер) надає вам коробку з усіма необхідними деталями (бінами) та інструментами (залежностями), готовими до використання. Вам просто потрібно їх "впровадити" у потрібні місця.

---

# 🏷️ `@MyComponent` – Аналог `@Component`

`@MyComponent` позначає клас як компонент, керований `MyApplicationContext`. Це наш аналог `@Component` у Spring.

**Порівняння з реальним Spring:**
У Spring є спеціалізовані анотації, що є мета-анотаціями `@Component`:
* `@Service`: для бізнес-логіки.
* `@Repository`: для доступу до даних.
* `@Controller` / `@RestController`: для HTTP-запитів.

---

# 💡 Приклад `@MyComponent` з життя

`@MyComponent` — це як загальна позначка "Працівник".

`@Service`, `@Repository`, `@Controller` — це більш конкретні ролі: "Бухгалтер", "Менеджер з продажу", "Водій". Всі вони є "Працівниками", але мають свої специфічні обов'язки.

---

# 🏷️ `@MyAutowired` – Аналог `@Autowired`

`@MyAutowired` використовується для автоматичного впровадження залежностей. Контейнер знаходить бін потрібного типу та присвоює його полю. Це наш аналог `@Autowired`.

**Порівняння з реальним Spring:**
`@Autowired` є основним механізмом DI. Spring рекомендує впровадження через конструктор, що робить залежності незмінними і полегшує тестування.

---

# 👨‍💻 `@Autowired` через конструктор

```java
// Приклад впровадження через конструктор в реальному Spring
@Service
public class RealUserService {
    private final RealLoggerService logger;

    // Spring автоматично впровадить RealLoggerService через конструктор
    public RealUserService(RealLoggerService logger) {
        this.logger = logger;
    }
    // ...
}
```
Spring автоматично впроваджує залежність через конструктор.

---

# 🏷️ `@MyScope` – Аналог `@Scope`

`@MyScope` визначає область видимості біна (`SINGLETON` або `PROTOTYPE`). Аналогічно `@Scope` в Spring.

**Порівняння з реальним Spring:**
Spring підтримує ці та інші області видимості: `request`, `session`, `application` (для веб-додатків).

---

# 🔄 Області Видимості: `SINGLETON`

`SINGLETON`: Один екземпляр біна створюється та перевикористовується протягом всього життя контейнера. Це типова поведінка для сервісів без стану.

**Коли використовувати:**
Більшість сервісів, репозиторіїв, контролерів є Singleton. Наприклад, `LoggerService` ідеальний для Singleton, оскільки не зберігає унікального стану.

---

# 🔄 Області Видимості: `PROTOTYPE`

`PROTOTYPE`: Новий екземпляр біна створюється кожного разу, коли його запитують. Це корисно для об'єктів зі станом, які повинні бути унікальними для кожного використання.

**Коли використовувати:**
Якщо об'єкт зберігає стан, специфічний для одного запиту або користувача, і ви не хочете, щоб цей стан впливав на інших. Наприклад, "кошик покупок" для кожного окремого клієнта.

---

# ⚙️ Головний клас: `SpringCreWithAnnotations.java`

Цей клас є точкою входу, що демонструє роботу `MyApplicationContext`.

```java
package com.lss;

import com.lss.context.MyApplicationContext;
import com.lss.services.UserService;

public class SpringCreWithAnnotations {
    public static void main( String[] args ) {
        // 🚀 Створюємо контекст, вказуючи базовий пакет для сканування компонентів
        MyApplicationContext context = new MyApplicationContext("com.lss.services");
        // 🎯 Отримуємо бін UserService з контексту.
        // Контейнер сам створить його та впровадить залежності.
        UserService userService = context.getBean(UserService.class);
        userService.doWork();
        // 📊 Виводимо статистику створених об'єктів
        ObjectTracker.print();
        // 📋 Виводимо інформацію про біни в контексті
        context.printBeans();
    }
}
```

---

# ✅ Пояснення `SpringCreWithAnnotations.java`

`MyApplicationContext context = new MyApplicationContext("com.lss.services");`: Ініціалізуємо контейнер, вказуючи, де шукати `@MyComponent` класи.

**Порівняння з реальним Spring:**
У реальному Spring ви б створили `AnnotationConfigApplicationContext` або використали `SpringApplication.run()` для Spring Boot.

```java
// Реальний Spring Boot application
@SpringBootApplication
public class MySpringApp {
    public static void main(String[] args) {
        SpringApplication.run(MySpringApp.class, args);
    }
}
```

---

# ✅ Пояснення `getBean()`

`UserService userService = context.getBean(UserService.class);`: Контейнер автоматично знаходить, створює та конфігурує `UserService` (і його залежності).

**Порівняння з реальним Spring:**
Це прямий виклик `getBean()`, що використовується для програмного отримання бінів. У більшості Spring-додатків ви будете використовувати `@Autowired` для впровадження бінів, а не отримувати їх вручну.

---

# ⚙️ Сервісний Шар: `LoggerService`

```java
package com.lss.services;

import com.lss.ObjectTracker;
import com.lss.annotation.MyComponent;

@MyComponent // 🏷️ Позначаємо як компонент, керований контейнером
public class LoggerService {

    public LoggerService() {
        ObjectTracker.created(getClass()); // 📊 Відстежуємо створення об'єкта
    }

    public void log(String msg) {
        System.out.println("[LOG] " + msg);
    }
}
```

`@MyComponent`: Це наш базовий компонент, створений як Singleton за замовчуванням.

---

# ✅ Пояснення `LoggerService`

**Порівняння з реальним Spring:**
У реальному Spring ви б, ймовірно, використали `@Service` для такого класу, оскільки він виконує сервісну функцію.

```java
// Реальний Spring LoggerService
@Service
public class RealLoggerService {
    public void log(String msg) {
        System.out.println("[REAL LOG] " + msg);
    }
}
```

---

# ⚙️ Сервісний Шар: `TimeService`

```java
package com.lss.services;

import com.lss.ObjectTracker;
import com.lss.annotation.MyAutowired; // 🏷️ Імпортуємо нашу анотацію
import com.lss.annotation.MyComponent;

@MyComponent // 🏷️ Позначаємо як компонент
public class TimeService {
    @MyAutowired // 🔌 Автоматично впроваджуємо LoggerService
    private LoggerService logger;

    public TimeService() {
        ObjectTracker.created(getClass()); // 📊 Відстежуємо створення об'єкта
    }

    public long now() {
        logger.log("Doing work at TimeService = " + System.currentTimeMillis());
        return System.currentTimeMillis();
    }
}
```

`@MyAutowired private LoggerService logger;`: Контейнер знайде `LoggerService` і впровадить його в це поле.

---

# ✅ Пояснення `TimeService`

**Порівняння з реальним Spring:**
Це типовий приклад впровадження залежностей через поле.

```java
// Реальний Spring TimeService
@Service
public class RealTimeService {
    @Autowired
    private RealLoggerService logger; // Впровадження через поле

    public long now() {
        logger.log("Doing work at RealTimeService = " + System.currentTimeMillis());
        return System.currentTimeMillis();
    }
}
```

---

# ⚙️ Сервісний Шар: `UserService`

```java
package com.lss.services;

import com.lss.ObjectTracker;
import com.lss.annotation.MyAutowired; // 🏷️ Імпортуємо нашу анотацію
import com.lss.annotation.MyComponent;

@MyComponent // 🏷️ Позначаємо як компонент
public class UserService {

    @MyAutowired // 🔌 Впроваджуємо LoggerService
    private LoggerService logger;

    @MyAutowired // 🔌 Впроваджуємо TimeService
    private TimeService timeService;

    public UserService() {
        ObjectTracker.created(getClass()); // 📊 Відстежуємо створення об'єкта
    }

    public void doWork() {
        logger.log("Doing work at " + timeService.now());
    }
}
```
`@MyAutowired`: `UserService` залежить від `LoggerService` та `TimeService`. Контейнер автоматично розв'язує ці залежності.

---

# ✅ Пояснення `UserService`

**Порівняння з реальним Spring:**
Це класичний приклад сервісу, що використовує інші сервіси. У реальному світі `UserService` може взаємодіяти з `UserRepository` (для доступу до БД) або `EmailService`.

```java
// Реальний Spring UserService
@Service
public class RealUserService {
    @Autowired
    private RealLoggerService logger;
    @Autowired
    private RealTimeService timeService;
    // @Autowired
    // private UserRepository userRepository; // Можлива залежність від репозиторію

    public void doWork() {
        logger.log("Doing real work at " + timeService.now());
        // userRepository.saveUser(...); // Приклад використання репозиторію
    }
}
```

---

# 🔄 Взаємодія між Контролером та Сервісами

У нашому прикладі `SpringCreWithAnnotations` виконує роль "контролера" або "точки запуску", що запитує `UserService` з контексту. `UserService` використовує `LoggerService` та `TimeService` для бізнес-логіки.

**Порівняння з реальним Spring MVC:**
У реальному Spring MVC `@RestController` обробляє HTTP-запити та делегує бізнес-логіку сервісам.

---

# 👨‍💻 Реальний Spring REST Controller

```java
// Приклад реального Spring REST Controller
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private RealUserService userService; // Впроваджуємо сервіс

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // Контролер делегує бізнес-логіку сервісу
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

---

# 📌 Зв'язки Controller-Service-Repository

* **Controller (`UserController`)**: Приймає HTTP-запити, валідує дані та повертає відповідь. Він **делегує** бізнес-логіку сервісному шару.
* **Service (`UserService`)**: Містить основну бізнес-логіку. Він **використовує** репозиторії для взаємодії з даними та може використовувати інші сервіси.
* **Repository (`UserRepository`)**: Відповідає за взаємодію з базою даних (CRUD-операції). Він **надає** дані сервісам.

---

# 💡 Приклад зв'язків з життя: Ресторан

Уявіть ресторан:

* **Клієнт** робить замовлення (HTTP-запит).
* **Офіціант (Controller)** приймає замовлення, перевіряє його і передає на кухню.
* **Шеф-кухар (Service)** готує страву, використовуючи інгредієнти. Може попросити помічника (інші сервіси) нарізати овочі.
* **Кладовщик (Repository)** надає інгредієнти шеф-кухарю.

---

# 🧠 DTO/Entity – Для чого вони?

Хоча в нашому проєкті немає явних DTO (Data Transfer Objects) або Entity класів, важливо розуміти їхню роль у реальних Spring-додатках, особливо при роботі з БД.

---

# 🧠 `Entity` (Сутність)

`Entity`: Представляє об'єкт, який зберігається в базі даних. Кожен екземпляр Entity зазвичай відповідає рядку в таблиці БД.
Часто позначається анотаціями `@Entity` (JPA/Hibernate) та `@Table`.

```java
// Реальна JPA Entity
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    // getters, setters, constructors
}
```

---

# 🧠 `DTO` (Data Transfer Object)

`DTO`: Об'єкт для передачі даних між різними шарами додатка або між додатком та зовнішнім світом (REST API).
Містить лише необхідні поля, може відрізнятися від Entity, щоб приховати внутрішню структуру БД.

```java
// Реальний DTO для відображення користувача
public class UserDto {
    private Long id;
    private String username;
    // Не показуємо email або пароль, якщо це не потрібно клієнту
    // getters, setters, constructors
}
```

---

# ✅ Для чого потрібні DTO/Entity?

* **Розділення відповідальностей:** Entity для персистентності, DTO для передачі даних.
* **Безпека:** DTO контролюють, які дані виставляються назовні, запобігаючи витоку конфіденційної інформації.
* **Гнучкість:** Можна мати різні DTO для різних операцій (`UserCreationDto`, `UserUpdateDto`).
* **Уникнення циклічних залежностей:** DTO допомагають уникнути нескінченних циклів при серіалізації в JSON.

---

# 🧱 Архітектурні Принципи

Анотації та IoC-контейнер допомагають дотримуватися принципів.

---

# 🧠 Dependency Inversion Principle (DIP)

DIP: Залежність від абстракцій (через типи полів, а не конкретне `new`), а не від конкретних класів. Наш контейнер "інвертує" контроль.

**Приклад з життя:**
Замість того, щоб купувати новий молоток кожного разу, ви просите "інструмент для забивання цвяхів" (абстракція), а помічник (контейнер) дає вам потрібний молоток.

---

# 🧠 Single Responsibility Principle (SRP)

SRP: Кожен сервіс (`LoggerService`, `TimeService`, `UserService`) має чітко визначену відповідальність, а контейнер займається лише життєвим циклом та залежностями.

**Приклад з життя:**
На кухні ресторану є окремі люди, які відповідають за миття посуду, нарізку овочів, приготування м'яса. Кожен має свою одну відповідальність, що робить процес ефективнішим.

---

# 🧠 Open/Closed Principle (OCP)

OCP: Код повинен бути відкритим для розширення, але закритим для модифікації. Завдяки DI, ви можете легко замінити одну реалізацію сервісу іншою, не змінюючи код `UserService`.

**Приклад з життя:**
Якщо ви хочете змінити постачальника кави для своєї кав'ярні, ви просто укладаєте договір з новим постачальником, не переробляючи всю кав'ярню.

---

# ❓ Запитання для самоперевірки

* Які переваги дає використання анотацій для управління бінами порівняно з ручним створенням об'єктів? Наведіть приклад, де це критично.
* Як `MyApplicationContext` відрізняє `SINGLETON` від `PROTOTYPE` бінів, і чому це важливо? У яких сценаріях ви б обрали `PROTOTYPE` у реальному Spring?
* Опишіть покроково, як `UserService` отримує залежності. Як це відрічалося б з впровадженням через конструктор у реальному Spring?

---