package com.allen.component.easyexport;

import com.allen.component.common.exception.BizException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 16:16
 */
@AllArgsConstructor
public class AnnotationConfigStrategy implements ConfigParse{

    private SupportExcelExport supportExcelExport;

    @Override
    public ExportConfigStrategy getStrategy() {
        return ExportConfigStrategy.AnnotationConfig;
    }

    @Override
    public List<ExportFieldConfig> parse() {
        ExportColumnConfig[] configs = supportExcelExport.exportColumnConfig();
        if(configs == null || configs.length == 0){
            throw new BizException("AnnotationConfig ExportConfigStrategy requires ExportColumnConfig's length > 0");
        }
        List<ExportFieldConfig> fieldConfigs = new ArrayList<>();
        for(ExportColumnConfig config : configs){
            fieldConfigs.add(ExportFieldConfig.builder()
                    .fieldName(config.fieldName())
                    .columnName(config.columnName())
                    .build());
        }
        return fieldConfigs;
    }
}
