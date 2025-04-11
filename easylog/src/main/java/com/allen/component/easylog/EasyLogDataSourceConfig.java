package com.allen.component.easylog;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 11:31
 */
@Configuration
public class EasyLogDataSourceConfig {

    @Bean("easyLogDataSource")
    @Conditional(DatabaseConfigCondition.class)
    public DataSource easyLogDataSource(EasyLogProperties easyLogProperties) {
        EasyLogProperties.DbConfig dbConfig = easyLogProperties.getDbConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbConfig.getUrl());
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        config.setDriverClassName(dbConfig.getDriverClassName());

        return new HikariDataSource(config);
    }

    @Bean("easyLogDAO")
    @ConditionalOnBean(name = "easyLogDataSource")
    public EasyLogDAO easyLogDAO(DataSource easyLogDataSource){
        return new EasyLogDAO(easyLogDataSource);
    }
}
