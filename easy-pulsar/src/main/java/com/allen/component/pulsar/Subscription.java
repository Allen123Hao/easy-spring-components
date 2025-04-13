package com.allen.component.pulsar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 17:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscription {

    /**
     * 消费订阅的名称，允许为空，默认为consumerName + "-sub"
     * @return
     */
    String name() default "";
}
