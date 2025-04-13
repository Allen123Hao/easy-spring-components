package com.allen.component.easyexport;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 10:58
 */
@Configuration
public class EasyExportDataSourceConfig {

    @Autowired
    private EasyExportProperties easyExportProperties;

    @Bean("easyExportDataSource")
    @Conditional(DatabaseConfigCondition.class)
    public DataSource easyExportDataSource() {
        EasyExportProperties.DbConfig dbConfig = easyExportProperties.getDbConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbConfig.getUrl());
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        config.setDriverClassName(dbConfig.getDriverClassName());

        return new HikariDataSource(config);
    }

}
