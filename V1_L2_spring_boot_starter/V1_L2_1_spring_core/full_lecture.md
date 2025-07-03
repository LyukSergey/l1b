<!-----



Conversion time: 1.154 seconds.


Using this Markdown file:

1. Paste this output into your source file.
2. See the notes and action items below regarding this conversion run.
3. Check the rendered output (headings, lists, code blocks, tables) for proper
   formatting and use a linkchecker before you publish this page.

Conversion notes:

* Docs to Markdown version 1.0β44
* Fri Jun 20 2025 02:56:56 GMT-0700 (PDT)
* Source doc: L4-spring-core

WARNING:
You have 26 H1 headings. You may want to use the "H1 -> H2" option to demote all headings by one level.

----->


<p style="color: red; font-weight: bold">>>>>>  gd2md-html alert:  ERRORs: 0; WARNINGs: 1; ALERTS: 0.</p>
<ul style="color: red; font-weight: bold"><li>See top comment block for details on ERRORs and WARNINGs. <li>In the converted Markdown or HTML, search for inline alerts that start with >>>>>  gd2md-html alert:  for specific instances that need correction.</ul>

<p style="color: red; font-weight: bold">Links to alert messages:</p>
<p style="color: red; font-weight: bold">>>>>> PLEASE check and correct alert issues and delete this message and the inline alerts.<hr></p>



# **📘 Spring Core: Поглиблене Занурення**


---


# **📌 Завдання лекції**



* Зрозуміти розширений **життєвий цикл біна** у Spring.
* Дослідити роботу **BeanPostProcessor** та **BeanFactoryPostProcessor**.
* Дізнатися, як створювати **власні Scope** для бінів.
* Ознайомитися з **аспектами та AOP** на прикладі профайлінгу.
* Розглянути **кастомні анотації** та їх обробку.


---


# **🧠 Контейнер Spring: Рекап**

Контейнер Spring відповідає за створення, налаштування та управління життєвим циклом об'єктів (бінів).

Ми вже знаємо про Dependency Injection (DI) та різні Scope (Singleton, Prototype). Сьогодні ми поглибимо наші знання про його внутрішню роботу.


---


# **🔄 Життєвий Цикл Біна у Spring**

Бін у Spring проходить кілька фаз від створення до знищення. Це дозволяє нам "врізатися" в ці етапи для виконання власної логіки.



1. **Інстанціювання:** Створення екземпляра біна (конструктор).
2. **Залежності:** Встановлення властивостей (сеттери) та впровадження залежностей.
3. **Ініціалізація:** Виконання ініціалізаційної логіки (@PostConstruct, InitializingBean).
4. **Використання:** Бін готовий до роботи.
5. **Знищення:** Звільнення ресурсів (@PreDestroy, DisposableBean).


---


# **👨‍💻 Бін: TerminatorQuoter.java**

Це наш основний бін, на якому ми продемонструємо життєвий цикл та кастомні анотації.

Java

package com.lss.beans;

import com.lss.annotations.InjectRandomInt;

import com.lss.annotations.PostProxy;

import com.lss.annotations.Profiling;

import jakarta.annotation.PostConstruct;

@Profiling

public class TerminatorQuoter implements Quoter {

    private String message;

    @InjectRandomInt(min = 1, max = 10)

    private int repeat;

    @PostConstruct // 💡 Метод ініціалізації

    public void init() {

        System.out.printf("Phase 2. TerminatorQuoter init method %s%n", repeat);

    }

    public TerminatorQuoter() {

        System.out.printf("Phase 1. TerminatorQuoter constructor %s%n", repeat);

    }

    @Override

    @PostProxy // 💡 Кастомна анотація для пост-проксі виклику

    public void sayQuote() {

        System.out.printf("Phase 3. TerminatorQuoter sayQuote %s%n", repeat);

        for (int i = 0; i &lt; repeat; i++) {

            System.out.printf("message = %s_%s%n", message, i);

        }

    }

}


---


# **✅ Фази Життєвого Циклу: Приклад**

У TerminatorQuoter.java:



* **Phase 1. Constructor:** Викликається першим, створюючи об'єкт.
* **Phase 2. @PostConstruct init() метод:** Викликається після того, як конструктор завершив роботу та всі @InjectRandomInt (і інші) поля були встановлені.


---


# **⚙️ BeanPostProcessor (BPP)**

BPP - це інтерфейс Spring, що дозволяє втручатися в процес створення бінів **після виклику конструктора, але до/після методів ініціалізації**.



* Методи: postProcessBeforeInitialization() та postProcessAfterInitialization().
* Дозволяє модифікувати або обгортати (проксі) біни.


---


# **👨‍💻 BPP: InjectRandomIntAnnotationBeanPostProcessor**

Цей BPP обробляє нашу кастомну анотацію @InjectRandomInt.

Java

package com.lss.beanPostProcessors;

import com.lss.annotations.InjectRandomInt;

import java.lang.reflect.Field;

import java.util.Random;

import org.springframework.beans.factory.config.BeanPostProcessor;

import org.springframework.util.ReflectionUtils;

public class InjectRandomIntAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override

    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        final Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {

            final InjectRandomInt annotation = field.getAnnotation(InjectRandomInt.class);

            if (annotation != null) {

                final int min = annotation.min();

                final int max = annotation.max();

                Random random = new Random();

                int i = random.nextInt(max - min + 1) + min;

                field.setAccessible(true);

                ReflectionUtils.setField(field, bean, i); // 💡 Встановлюємо випадкове значення

            }

        }

        return bean;

    }

    // postProcessAfterInitialization() не використовується в цьому прикладі

}


---


# **✅ InjectRandomIntAnnotationBeanPostProcessor: Пояснення**



* **@InjectRandomInt(min = 1, max = 10):** Ця анотація на полі repeat у TerminatorQuoter вказує діапазон для випадкового числа.
* **postProcessBeforeInitialization():** BPP перехоплює бін до його ініціалізації. Він сканує поля на наявність @InjectRandomInt, генерує випадкове число та впроваджує його в поле.
* Це дозволяє динамічно конфігурувати біни без зміни їхнього вихідного коду.


---


# **👨‍💻 BPP: ProfilingHandlerBeanPostProcessor**

Цей BPP реалізує базовий профайлінг для бінів, позначених @Profiling. Він використовує JMX.

Java

package com.lss.beanPostProcessors;

import com.lss.annotations.Profiling;

import com.lss.controllers.ProfilingController;

import java.lang.management.ManagementFactory;

import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;

import java.lang.reflect.Proxy;

import java.util.HashMap;

import java.util.Map;

import javax.management.ObjectName;

import org.springframework.beans.BeansException;

import org.springframework.beans.factory.config.BeanPostProcessor;

public class ProfilingHandlerBeanPostProcessor implements BeanPostProcessor {

    private Map&lt;String, Class> map = new HashMap&lt;>();

    private ProfilingController profilingController = new ProfilingController();

    public ProfilingHandlerBeanPostProcessor() {

        // Реєстрація JMX MBean для керування профайлінгом

        final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {

            platformMBeanServer.registerMBean(profilingController, new ObjectName("profiling", "name", "controller"));

        } catch (Exception e) { /* ... */ }

    }

    @Override

    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        final Class&lt;?> beanClass = bean.getClass();

        if (beanClass.isAnnotationPresent(Profiling.class)) { // 💡 Зберігаємо класи, які потрібно профайлити

            map.put(beanName, beanClass);

        }

        return bean;

    }

    @Override

    public Object postProcessAfterInitialization(Object bean, String beanName) {

        final Class beanClass = map.get(beanName);

        if (beanClass != null) {

            // 💡 Створюємо динамічний проксі для профайлінгу

            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {

                @Override

                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

                    if (profilingController.isEnabled()) { // 💡 Перевіряємо, чи профайлінг увімкнений через JMX

                        System.out.println("Start profiling...");

                        final long before = System.nanoTime();

                        final Object retVal = method.invoke(bean, objects);

                        final long after = System.nanoTime();

                        System.out.println("End profiling. " + (after - before));

                        return retVal;

                    }

                    return method.invoke(bean, objects);

                }

            });

        }

        return bean;

    }

}


---


# **✅ ProfilingHandlerBeanPostProcessor: Пояснення**



* **@Profiling:** Позначає біни, для яких потрібен профайлінг (у нашому випадку TerminatorQuoter).
* **postProcessAfterInitialization():** BPP перехоплює бін після його ініціалізації. Якщо бін позначений @Profiling, BPP створює **динамічний проксі** (з використанням java.lang.reflect.Proxy) навколо нього.
* **Проксі:** При виклику методу оригінального біна, проксі вимірює час виконання, якщо профайлінг увімкнено.
* **JMX:** ProfilingController реєструється як MBean у JMX, дозволяючи керувати ввімкненням/вимкненням профайлінгу "на льоту".


---


# **⚙️ BeanFactoryPostProcessor (BFPP)**

BFPP - це інтерфейс Spring, що дозволяє втручатися в процес конфігурації контейнера **до того, як біни будуть інстанційовані**. Він модифікує **метадані (BeanDefinition)**, а не самі екземпляри бінів.



* Метод: postProcessBeanFactory().
* Дозволяє змінювати тип класу біна, його залежності, scope тощо.


---


# **👨‍💻 BFPP: DeprecationHandlerBeanFactoryPostProcessor**

Цей BFPP обробляє нашу кастомну анотацію @DeprecatedClass.

Java

package com.lss.beanFactoryPostProcessors;

import com.lss.annotations.DeprecatedClass;

import org.springframework.beans.factory.config.BeanDefinition;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class DeprecationHandlerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

        final String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (String beanDefinitionName : beanDefinitionNames) {

            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);

            final String beanClassName = beanDefinition.getBeanClassName();

            try {

                final Class&lt;?> beanClass = Class.forName(beanClassName);

                final DeprecatedClass annotation = beanClass.getAnnotation(DeprecatedClass.class);

                if (annotation != null) { // 💡 Якщо клас позначений @DeprecatedClass

                    beanDefinition.setBeanClassName(annotation.newImpl().getName()); // 💡 Замінюємо клас на новий

                }

            } catch (ClassNotFoundException e) { /* ... */ }

        }

    }

}


---


# **✅ DeprecationHandlerBeanFactoryPostProcessor: Пояснення**



* **@DeprecatedClass(newImpl = T1000.class):** Ця анотація на TerminatorQuoter вказує, що його слід замінити на T1000.class.
* **postProcessBeanFactory():** BFPP викликається на ранній стадії, до інстанціювання бінів. Він сканує BeanDefinition'и.
* Якщо BeanDefinition відноситься до класу, позначеного @DeprecatedClass, BFPP **змінює назву класу в BeanDefinition** на newImpl(), що призводить до інстанціювання T1000 замість TerminatorQuoter.
* Це демонструє потужність BFPP для глобальної модифікації поведінки бінів без зміни їхніх оригінальних класів.


---


# **⚙️ Кастомні Scope**

Spring дозволяє створювати власні механізми управління життєвим циклом бінів, реалізуючи інтерфейс org.springframework.beans.factory.config.Scope.


---


# **👨‍💻 Кастомний Scope: PeriodicalScopeConfigurer**

Цей клас реалізує Scope, який створює новий екземпляр біна кожні 3 секунди.

Java

package com.screenSaver;

import static java.time.LocalTime.now;

import java.time.LocalTime;

import java.util.HashMap;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.ObjectFactory;

import org.springframework.beans.factory.config.Scope;

public class PeriodicalScopeConfigurer implements Scope {

    private Map&lt;String, Pair&lt;LocalTime, Object>> map = new HashMap&lt;>();

    @Override

    public Object get(String name, ObjectFactory&lt;?> objectFactory) {

        // 💡 Логіка створення нового екземпляра кожні 3 секунди

        if (map.containsKey(name)) {

            final Pair&lt;LocalTime, Object> pair = map.get(name);

            final int secondsSinceLastRequest = now().getSecond() - pair.getKey().getSecond();

            if (secondsSinceLastRequest > 3) {

                map.put(name, Pair.of(now(), objectFactory.getObject())); // Створюємо новий

            }

        } else {

            map.put(name, Pair.of(now(), objectFactory.getObject())); // Створюємо вперше

        }

        return map.get(name).getValue();

    }

    // Інші методи інтерфейсу Scope...

}


---


# **👨‍💻 Реєстрація Кастомного Scope: CustomScopeRegistryBeanFactoryPostProcessor**

Цей BFPP реєструє наш PeriodicalScopeConfigurer у контейнері.

Java

package com.screenSaver;

import org.springframework.beans.BeansException;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.springframework.stereotype.Component;

@Component

public class CustomScopeRegistryBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        // 💡 Реєструємо наш кастомний scope під назвою "periodical"

        beanFactory.registerScope("periodical", new PeriodicalScopeConfigurer());

    }

}


---


# **👨‍💻 Використання Кастомного Scope: Config.java**

Приклад використання Periodical scope для біна Color.

Java

package com.screenSaver;

import java.awt.Color;

import java.util.Random;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Scope; // Імпорт стандартної анотації @Scope

@Configuration

@ComponentScan(basePackages = "com.screenSaver")

public class Config {

    @Bean

    @Scope(value = "periodical") // 💡 Вказуємо наш кастомний scope

    public Color color() {

        final Random random = new Random();

        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));

    }

    @Bean

    public ColorFrame colorFrame() {

        return new ColorFrame() {

            @Override

            protected Color getColor() {

                return color(); // 💡 Отримуємо бін Color

            }

        };

    }

    public static void main(String[] args) throws InterruptedException {

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        while (true) {

            context.getBean(ColorFrame.class).showOnRandomPlace();

            Thread.sleep(300L); // 💡 Кожні 300 мс оновлюємо кадр

        }

    }

}


---


# **✅ Кастомний Scope: Пояснення**



* **PeriodicalScopeConfigurer:** Цей Scope перевизначає метод get(), який відповідає за повернення екземпляра біна. Він створює новий Color кожні 3 секунди, інакше повертає існуючий.
* **CustomScopeRegistryBeanFactoryPostProcessor:** Реєструє цей Scope під назвою "periodical" у Spring BeanFactory.
* **@Scope("periodical"):** Анотація на Color біні змушує Spring використовувати наш кастомний Scope.
* **Результат:** Колір фрейму змінюється лише раз на 3 секунди, хоча ColorFrame отримує Color бін кожні 300 мс. Це демонструє повний контроль над життєвим циклом бінів.


---


# **⚙️ Application Listeners**

Spring ApplicationListener дозволяє реагувати на події, що відбуваються в контексті Spring, наприклад, ContextRefreshedEvent (коли контекст повністю завантажений).


---


# **👨‍💻 Listener: PostProxyInvokerContextListener**

Цей Listener обробляє нашу кастомну анотацію @PostProxy.

Java

package com.lss.listeners;

import com.lss.annotations.PostProxy;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.config.BeanDefinition;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.springframework.context.ApplicationContext;

import org.springframework.context.ApplicationListener;

import org.springframework.context.event.ContextRefreshedEvent;

public class PostProxyInvokerContextListener implements ApplicationListener&lt;ContextRefreshedEvent> {

    @Autowired

    private ConfigurableListableBeanFactory beanFactory;

    @Override

    public void onApplicationEvent(ContextRefreshedEvent event) {

        final ApplicationContext applicationContext = event.getApplicationContext();

        final String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();

        for (String beanDefinitionName : beanDefinitionNames) {

            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);

            final String beanClassName = beanDefinition.getBeanClassName();

            try {

                final Class&lt;?> originalClass = Class.forName(beanClassName);

                final Method[] methods = originalClass.getMethods();

                for (Method method : methods) {

                    if (method.isAnnotationPresent(PostProxy.class)) { // 💡 Шукаємо методи з @PostProxy

                        final Object bean = applicationContext.getBean(beanDefinitionName); // Отримуємо проксі-об'єкт

                        // Важливо викликати метод саме на проксі-об'єкті, а не оригінальному класі

                        final Method currentMethod = bean.getClass().getMethod(method.getName(), method.getParameterTypes());

                        currentMethod.invoke(bean); // 💡 Викликаємо метод

                    }

                }

            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {

                throw new RuntimeException(e);

            }

        }

    }

}


---


# **✅ PostProxyInvokerContextListener: Пояснення**



* **@PostProxy:** Анотація на методі sayQuote() у TerminatorQuoter.
* **onApplicationEvent(ContextRefreshedEvent):** Коли Spring-контекст повністю ініціалізований, цей метод викликається.
* Listener проходить по всіх бінах, знаходить методи, позначені @PostProxy, і викликає їх. Це важливо, оскільки на цьому етапі всі проксі (створені BPP) вже готові, і виклик відбувається саме на проксі-об'єкті.
* Це дозволяє виконувати дії над повністю налаштованими та проксірованими бінами після старту контексту.


---


# **💡 Ключові Анотації в L4-spring-core**



* **@Profiling**: Для включення вимірювання часу виконання методів біна через ProfilingHandlerBeanPostProcessor.
* **@InjectRandomInt**: Для ін'єкції випадкового цілого числа в поля біна через InjectRandomIntAnnotationBeanPostProcessor.
* **@PostProxy**: Для виклику методу на біні (після того, як всі проксі створені) після оновлення контексту через PostProxyInvokerContextListener.
* **@DeprecatedClass**: Для заміни класу біна на іншу реалізацію на етапі BeanFactoryPostProcessor.


---


# **🧠 SOLID та Патерни Проєктування**

Цей проєкт демонструє:



* **AOP (Aspect-Oriented Programming)**: ProfilingHandlerBeanPostProcessor є прикладом AOP. Він відокремлює "профайлінг" (крос-функціональну вимогу) від основної бізнес-логіки. Це реалізується за допомогою **Proxy Pattern**.
* **Factory Pattern**: Сам Spring IoC-контейнер є реалізацією Factory Pattern, відповідаючи за створення та управління об'єктами.
* **Decorator Pattern**: Динамічні проксі, створені BPP, можна розглядати як форму Decorator Pattern, що додає функціональність (профайлінг) до існуючих об'єктів.


---


# **❓ Запитання для самоперевірки**



* У чому ключова відмінність між BeanPostProcessor та BeanFactoryPostProcessor? Наведіть приклад їх використання з цього проєкту.
* Поясніть, як @InjectRandomInt працює "під капотом" завдяки BeanPostProcessor.
* Як реалізовано профайлінг у цьому проєкті? Як JMX дозволяє керувати ним?
* Яку проблему вирішує DeprecationHandlerBeanFactoryPostProcessor з @DeprecatedClass? Чому це є потужним механізмом?


---


# **❓ Запитання для обговорення (продовження)**



* Опишіть логіку роботи кастомного Scope PeriodicalScopeConfigurer. Де такий Scope може бути корисним у реальному проєкті?
* Яку роль відіграє PostProxyInvokerContextListener? Чому важливо викликати @PostProxy методи саме на проксі-об'єктах?
* Наведіть приклади інших крос-функціональних вимог (крім профайлінгу), які можна було б реалізувати за допомогою BeanPostProcessor у Spring.