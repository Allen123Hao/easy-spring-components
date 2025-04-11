package com.allen.component.easyexport;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.allen.component.common.exception.Assert;
import com.allen.component.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 18:03
 */
@Slf4j
public class DynamicConfigUtil {

    public static String getValue(EasyExportProperties.NacosConfig nacosConfig,String key){
        //nacos服务配置
        String serverAddr = nacosConfig.getServerAddr();
        String dataId = nacosConfig.getDataId();
        String namespace = nacosConfig.getNamespace();
        String group = nacosConfig.getGroup();
        log.info("DynamicConfigUtil nacos getValue,serverAddr:{},dataId:{},namespace:{},group:{}",
                serverAddr,dataId,namespace,group);
        // 创建Nacos配置服务的配置
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace",namespace);
        String configs = null;
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            configs = configService.getConfig(dataId,group,5000);
        } catch (NacosException e) {
            log.error("nacos config error");
        }
        if(StringUtils.isEmpty(configs)){
            return null;
        }
        String[] configLines = configs.split("\n");
        String suffix = key.concat("=");
        String value = null;
        for (String line : configLines) {
            if (line.startsWith(suffix)) {
                value = line.substring(line.indexOf("=") + 1);
                break;
            }
        }
        return value;
    }

    public static String getValue(EasyExportProperties.DbConfig dbConfig,String key){
        String value = null;
        //创建数据源
//        DataSource dataSource = new SimpleDataSource(dbConfig.getUrl(),dbConfig.getUsername(),dbConfig.getPassword(),dbConfig.getDriverClassName());
        //使用连接池的方式
        DataSource dataSource = SpringUtil.getBean("easyExportDataSource",DataSource.class);
        try {
            // 使用数据源
            Db db = Db.use(dataSource);
            // 查询数据库，获取所有记录
            List<Entity> list = db.findBy(dbConfig.getTableName(),"uuid",key);
            Assert.notEmpty(list,"easy export not find db config data");
            value = list.get(0).getStr(dbConfig.getColumnName());
        } catch (Exception e) {
            log.error("easy export dynamic config parse db config error",e);
            throw new BizException("easy export dynamic config parse db config error");
        }
        return value;
    }
}
