package com.allen.component.easyexport;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/25 18:39
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "easy-export.dynamic-config")
public class EasyExportProperties {

    private final NacosConfig nacosConfig = new NacosConfig();

    private final DbConfig dbConfig = new DbConfig();

    // Nacos配置
    @Data
    public static class NacosConfig {
        private String serverAddr;
        private String dataId;
        private String namespace;
        private String group;
    }

    // 数据库配置
    @Data
    public static class DbConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        //存储动态配置的表
        private String tableName;
        //存储动态配置的列
        private String columnName;
    }

}
