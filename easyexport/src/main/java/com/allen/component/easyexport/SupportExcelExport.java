package com.allen.component.easyexport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/19 11:45
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportExcelExport {

    /**
     * 注解方式或动态配置方式
     * @return
     */
    ExportConfigStrategy configStrategy();

    /**
     * 是否分页
     * @return
     */
    boolean enablePaging();

    /**
     * 导出的文件名，最终会是文件名+时间戳的形式
     * @return
     */
    String exportFileName() default "";

    /**
     * 导出文件名的调用方法名
     * @return
     */
    String exportFileNameMethod() default "";

    /**
     * 注解方式，必填，导出列配置
     * @return
     */
    ExportColumnConfig[] exportColumnConfig() default {};

    /**
     * 动态配置方式，支持nacos的动态配置
     * @return
     */
    String configKey() default "";

    /**
     * 动态配置方式，支持数据库的动态配置
     * @return
     */
    String configUuid() default "";

    /**
     * 导出任务的执行方式，默认同步
     * @return
     */
    ExportExecuteType exportExecuteType() default ExportExecuteType.SYNC;



}
