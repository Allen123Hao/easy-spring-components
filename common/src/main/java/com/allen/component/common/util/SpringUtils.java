package com.allen.component.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 13:22
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = applicationContext;
        }
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static String getProp(String propertyName) {
        Environment env = applicationContext.getEnvironment();
        return env.getProperty(propertyName);
    }

    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        // 获取所有被注解标记的bean的名称
        return applicationContext.getBeanNamesForAnnotation(annotationType);

    }

    public static Map<String,Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType){
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    public static String[] getEnv(){
        Environment env = applicationContext.getEnvironment();
        String[] profiles = env.getActiveProfiles();
        return profiles;
    }

}
