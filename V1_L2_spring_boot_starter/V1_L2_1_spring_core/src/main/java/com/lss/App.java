package com.lss;

import com.lss.beans.HelloWorld;
import com.lss.beans.HelloWorldInterface;
import com.lss.beans.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        exampleOfGettingBean(context, true);
        profilingExample(context, false);
        context.getBean(Student.class).sayQuote();
    }

    private static void profilingExample(ApplicationContext context, boolean turnOn) throws InterruptedException {
        while (turnOn) {
            Thread.sleep(100);
            // Потрібно, використовувати інтерфейс, щоб не було проблем.
            // Обєкт класу може бути замінений на proxy-об'єкт
            // A інтерфейс завжди буде однаковий
            context.getBean(Student.class).sayQuote();
        }
    }

    private static void exampleOfGettingBean(ApplicationContext context, boolean turnOn) {
        if (turnOn) {
            HelloWorld obj1 = (HelloWorld) context.getBean("helloWorld");
            HelloWorld obj2 = context.getBean(HelloWorld.class);
            HelloWorld obj3 = (HelloWorld) context.getBean(HelloWorldInterface.class, "helloWorld");
            obj3.getMessage();
        }
    }
}
