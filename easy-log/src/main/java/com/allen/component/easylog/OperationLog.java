package com.allen.component.easylog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/6 14:48
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 服务名，默认配置文件读取
     * @return
     */
    String serviceName() default "";

    /**
     * 模块名，默认配置文件读取
     * @return
     */
    String moduleName() default "";

    /**
     * 接口名，默认使用类名#方法名
     * @return
     */
    String apiName() default "";

    /**
     * 操作类型
     * @return
     */
    String operationType();

    /**
     * 修改内容
     * @return
     */
    String content() default "";

}
