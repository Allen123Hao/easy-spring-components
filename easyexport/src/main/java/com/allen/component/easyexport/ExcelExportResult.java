package com.allen.component.easyexport;

import java.util.List;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/19 14:02
 */
public interface ExcelExportResult<T> {

    Integer getTotalPage();

    /**
     * 获取导出的数据集(分页情况下的每页数据)
     * @return
     */
    List<T> getExportList();

    void setTaskUuid(String taskUuid);

    String getTaskUuid();


}
