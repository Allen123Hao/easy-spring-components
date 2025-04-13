package com.allen.component.easylog;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 11:12
 */
public class DatabaseConfigCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 通过环境变量获取数据库配置
        String url = context.getEnvironment().getProperty("easy-log.db-config.url");
        String username = context.getEnvironment().getProperty("easy-log.db-config.username");
        String password = context.getEnvironment().getProperty("easy-log.db-config.password");
        String driverClassName = context.getEnvironment().getProperty("easy-log.db-config.driver-class-name");
        // 如果所有这些属性都被设置，则条件满足
        return url != null && username != null && password != null && driverClassName != null;
    }
}
