package com.allen.component.easyexport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/22 10:58
 */
@Target(ElementType.ANNOTATION_TYPE) // 使得该注解只能用在其他注解上
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportColumnConfig {

    /**
     * 对应实体类的属性名
     * @return
     */
    String fieldName();

    /**
     * Excel中的列名
     * @return
     */
    String columnName();
}
