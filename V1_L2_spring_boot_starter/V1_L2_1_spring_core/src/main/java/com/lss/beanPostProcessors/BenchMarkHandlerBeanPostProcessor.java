package com.lss.beanPostProcessors;

import com.lss.annotations.Benchmark;
import com.lss.controllers.ProfilingController;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BenchMarkHandlerBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Class> map = new HashMap<>();
    @Autowired
    private ProfilingController profilingController;

    /**
     * This method is called before the bean is initialized.
     * It checks if the bean class is annotated with @Benchmark and stores it in a map
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BenchMarkHandlerBeanPostProcessor.postProcessBeforeInitialization");
        final Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(Benchmark.class)) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    /**
     * This method is called after the bean has been initialized.
     * It wraps the bean in a proxy that measures the execution time of methods
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BenchMarkHandlerBeanPostProcessor.postProcessAfterInitialization");
        final Class beanClass = map.get(beanName);
        if (beanClass != null) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    if (profilingController.isEnabled()) {
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
