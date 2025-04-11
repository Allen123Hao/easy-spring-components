package com.allen.component.easyexport;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.allen.component.common.exception.BizException;
import com.allen.component.common.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 15:10
 */
@Slf4j
public class DynamicExportModel {

    // 使用Map存储字段值，key为columnName
    private Map<String, Object> values = new LinkedHashMap<>();

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValue(String columnName, Object value) {
        values.put(columnName, value);
    }

    // 根据配置动态生成注解
    public static List<List<String>> createExcelHead(List<ExportFieldConfig> fieldConfigs) {
        // 根据fieldConfigs的index排序确保列的顺序
        //fieldConfigs.sort(Comparator.comparingInt(ExportFieldConfig::getIndex));

        List<List<String>> head = new ArrayList<>();
        for (ExportFieldConfig config : fieldConfigs) {
            List<String> headColumn = new ArrayList<>();
            headColumn.add(config.getColumnName());
            head.add(headColumn);
        }
        return head;
    }

    // 生成动态数据
    public static List<DynamicExportModel> createExcelData(List<?> dataList, List<ExportFieldConfig> fieldConfigs) {
        List<DynamicExportModel> models = new ArrayList<>();
        for (Object data : dataList) {
            DynamicExportModel model = new DynamicExportModel();
            for (ExportFieldConfig config : fieldConfigs) {
                try {
                    Object value = ReflectionUtil.getNestedFieldValue(data,config.getFieldName());
                    model.setValue(config.getColumnName(), value);
                } catch (ReflectiveOperationException e) {
                    log.error("parse field from data error,fieldName:{},columnName:{}" +
                            "data:{}",config.getFieldName(),config.getColumnName(), JSONUtil.toJsonStr(data));
                    throw new BizException("parse field from data error");
                }
            }
            models.add(model);
        }
        return models;
    }

    public static List<List<Object>> convertSheetData(List<DynamicExportModel> models){
        List<Map<String, Object>> dataResult = models.stream().map(DynamicExportModel::getValues).toList();
        if(CollectionUtils.isEmpty(dataResult)){
            return new ArrayList<>();
        }
        Map<String,Object> map = dataResult.get(0);
        // 确定模拟数据key
        List<String> mapKey = map.keySet().stream().map(String::toString).collect(Collectors.toList());

        // 创建接收转换好数据的容器
        List<List<Object>> sheetData = new ArrayList<>();
        // 将模拟数据转换成需要的格式
        // 解释：map类型无法直接按照key值导入，需要转换成list类型的数据，依次导入
        for (Map<String,Object> m : dataResult) {
            List<Object> list = new ArrayList<>(mapKey.size());
            for (String colum : mapKey) {
                Object cellValue = convertCellValue(m.get(colum));
                list.add(cellValue);
            }
            sheetData.add(list);
        }
        return sheetData;
    }

    private static Object convertCellValue(Object value){
        if(value instanceof LocalDateTime v){
            return LocalDateTimeUtil.format(v, DatePattern.NORM_DATETIME_PATTERN);
        }
        if(value instanceof LocalDate v){
            return LocalDateTimeUtil.format(v, DatePattern.NORM_DATE_PATTERN);
        }
        return value;
    }
}
