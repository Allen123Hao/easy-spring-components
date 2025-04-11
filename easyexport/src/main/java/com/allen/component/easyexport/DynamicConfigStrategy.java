package com.allen.component.easyexport;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.allen.component.common.exception.Assert;
import com.allen.component.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 16:17
 */
@Slf4j
@AllArgsConstructor
public class DynamicConfigStrategy implements ConfigParse{

    private SupportExcelExport supportExcelExport;

    @Override
    public ExportConfigStrategy getStrategy() {
        return ExportConfigStrategy.DynamicConfig;
    }

    @Override
    public List<ExportFieldConfig> parse() {

        EasyExportProperties exportProperties = SpringUtil.getBean("easyExportProperties", EasyExportProperties.class);
        validateProperties(exportProperties);
        //Nacos配置key
        String configKey = supportExcelExport.configKey();
        //DB配置的uuid
        String configUuid = supportExcelExport.configUuid();
        String value = getDynamicValue(exportProperties,configKey,configUuid);
        Assert.notNull(value,"easy export can't get dynamic config value");
        List<ExportFieldConfig> configs = null;
        try {
            configs = JSONUtil.toList(value, ExportFieldConfig.class);
        } catch (Exception e) {
            log.error("easy export DynamicConfigStrategy parse config error,configKey:{},value:{}",
                    configKey,value);
            throw new BizException("easy export DynamicConfigStrategy parse config error");
        }
        return configs;
    }

    private String getDynamicValue(EasyExportProperties exportProperties,String configKey,String configUuid){
        if(StringUtils.isNotEmpty(configKey)){
            return DynamicConfigUtil.getValue(exportProperties.getNacosConfig(),configKey);
        }
        if(StringUtils.isNotEmpty(configUuid)){
            return DynamicConfigUtil.getValue(exportProperties.getDbConfig(),configUuid);
        }
        return null;
    }

    private void validateProperties(EasyExportProperties exportProperties){
        if(StringUtils.isNotEmpty(supportExcelExport.configKey())){
            Assert.notNull(exportProperties.getNacosConfig().getServerAddr(),"easy export nacos service address not config");
            Assert.notNull(exportProperties.getNacosConfig().getNamespace(),"easy export nacos namespace not config");
            Assert.notNull(exportProperties.getNacosConfig().getDataId(),"easy export nacos dataId not config");
            Assert.notNull(exportProperties.getNacosConfig().getGroup(),"easy export nacos group not config");
        }
        if(StringUtils.isNotEmpty(supportExcelExport.configUuid())){
            Assert.notNull(exportProperties.getDbConfig().getUrl(),"easy export db url not config");
            Assert.notNull(exportProperties.getDbConfig().getUsername(),"easy export db username not config");
            Assert.notNull(exportProperties.getDbConfig().getPassword(),"easy export db password not config");
            Assert.notNull(exportProperties.getDbConfig().getDriverClassName(),"easy export db driverClassName not config");
            Assert.notNull(exportProperties.getDbConfig().getTableName(),"easy export db table not config");
            Assert.notNull(exportProperties.getDbConfig().getColumnName(),"easy export db columnName not config");
        }
    }
}
