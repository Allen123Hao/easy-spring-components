package com.allen.component.easylog;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/7 10:52
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "easy-log")
public class EasyLogProperties {

    private final DefaultConfig defaultConfig = new DefaultConfig();

    private final DbConfig dbConfig = new DbConfig();

    // 默认配置
    @Data
    public static class DefaultConfig {
        private String serviceName;
        private String moduleName;
    }

    // 数据库配置
    @Data
    public static class DbConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }

}
