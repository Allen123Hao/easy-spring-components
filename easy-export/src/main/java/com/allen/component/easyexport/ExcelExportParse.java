package com.allen.component.easyexport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/24 16:05
 */
@Slf4j
@Data
@AllArgsConstructor
public class ExcelExportParse {

    private static final int pageSize = 50;

    private Object target;

    private Method method;

    private Object[] args;

    private ExcelWriter writer;

    private SseEmitter emitter;


    public void writer() throws Exception {

        SupportExcelExport supportExcelExport = method.getAnnotation(SupportExcelExport.class);

        boolean enablePaging = supportExcelExport.enablePaging();

        ConfigParse configParse = ConfigParseFactory.builder(supportExcelExport);
        List<ExportFieldConfig> fieldConfigs = configParse.parse();

        // 假设原方法返回的类型是List，实际中可能需要根据情况调整
        Class<?> returnType = method.getReturnType();
        if (!ExcelExportResult.class.isAssignableFrom(returnType)) {
            throw new IllegalStateException("easy export supported only for methods returning ExcelExportResult");
        }

        // 创建动态头部
        List<List<String>> head = DynamicExportModel.createExcelHead(fieldConfigs);

        WriteSheet writeSheet = EasyExcel.writerSheet("数据")
                .head(head)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .build();

        if(enablePaging){
            pagingWrite(writeSheet,fieldConfigs);
        }else{
            noPagingWrite(writeSheet,fieldConfigs);
        }
    }

    private void noPagingWrite(WriteSheet writeSheet,List<ExportFieldConfig> fieldConfigs) throws Exception{

        ExcelExportResult result = (ExcelExportResult) target.getClass().getMethod(method.getName(),method.getParameterTypes())
                .invoke(target, args);
        List<?> exportList = result.getExportList();
        // 创建动态数据
        List<DynamicExportModel> dynamicData = DynamicExportModel.createExcelData(exportList, fieldConfigs);
        List<List<Object>> sheetData = DynamicExportModel.convertSheetData(dynamicData);
        // 流式写入数据
        writer.write(sheetData,writeSheet);
        writer.finish();
    }

    private void pagingWrite(WriteSheet writeSheet,List<ExportFieldConfig> fieldConfigs) throws Exception{
        sendProgress(0d);
        int pageNo = 1;
        Integer totalPage = null;
        while(true){
            try {
                if(totalPage != null && pageNo > totalPage){
                    break;
                }
                adjustParams(args,pageNo);
                log.info("pagingWrite query pageNo:{}",pageNo);
                ExcelExportResult result = (ExcelExportResult) target.getClass().getMethod(method.getName(),method.getParameterTypes())
                        .invoke(target, args);
                log.info("pagingWrite result pageNo:{}",pageNo);
                if(result == null){
                    break;
                }
                if(totalPage == null){
                    totalPage = result.getTotalPage();
                }

                List<?> exportList = result.getExportList();
                if (exportList == null || exportList.isEmpty()) {
                    break;
                }
                // 创建动态数据
                List<DynamicExportModel> dynamicData = DynamicExportModel.createExcelData(exportList, fieldConfigs);
                List<List<Object>> sheetData = DynamicExportModel.convertSheetData(dynamicData);
                // 流式写入数据
                writer.write(sheetData,writeSheet);
            } catch (Exception e) {
                log.error("pagingWrite exception",e);
                break;
            }
            if(totalPage != null){
                log.info("sendProgress,pageNo:{},totalPage:{}",pageNo,totalPage);
                sendProgress((double)pageNo / (double)totalPage);
            }
            pageNo++;
        }
        writer.finish();
        log.info("pagingWrite finish");
        sendProgress(1d);
    }

    private void adjustParams(Object[] args,int pageNo){
        for (Object arg : args) {
            if (arg instanceof PageParam pageParam) {
                pageParam.setPageNo(pageNo);
                pageParam.setPageSize(pageSize);
            }
        }
    }

    private void sendProgress(double progress){
        if(emitter == null){
            return;
        }
        try {
            emitter.send(SseEmitter.event().name("progress").data(String.format("%.2f", progress)));
        } catch (Exception e) {
            log.error("SSE send progress error",e);
        }
    }


}
