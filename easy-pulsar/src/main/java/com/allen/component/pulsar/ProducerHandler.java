package com.allen.component.pulsar;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 12:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ProducerHandler {

    /**
     * 生产者名称
     * @return
     */
    String name() default "";

    /**
     * 主题
     * @return
     */
    String topic();
}
