package com.allen.component.easyexport;

import lombok.Builder;
import lombok.Data;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 15:09
 */
@Data
@Builder
public class ExportFieldConfig {

    // 对应实体类的属性名
    private String fieldName;

    // Excel中的列名
    private String columnName;

}
