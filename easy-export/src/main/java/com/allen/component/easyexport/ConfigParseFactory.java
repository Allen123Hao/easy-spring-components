package com.allen.component.easyexport;


import com.allen.component.common.exception.BizException;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 16:26
 */
public class ConfigParseFactory {

    public static ConfigParse builder(SupportExcelExport supportExcelExport){
        ExportConfigStrategy strategy = supportExcelExport.configStrategy();
        if(ExportConfigStrategy.AnnotationConfig == strategy){
            return new AnnotationConfigStrategy(supportExcelExport);
        }
        if(ExportConfigStrategy.DynamicConfig == strategy){
            return new DynamicConfigStrategy(supportExcelExport);
        }
        throw new BizException("not support ExportConfigStrategy:"+strategy);
    }
}
