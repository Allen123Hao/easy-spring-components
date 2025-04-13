package com.allen.component.easyexport;

import java.util.List;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 16:18
 */
public interface ConfigParse {

    ExportConfigStrategy getStrategy();

    List<ExportFieldConfig> parse();
}
