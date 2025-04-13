package com.allen.component.easyexport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.allen.component.common.exception.BizException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/9/14 15:48
 */
@Slf4j
public class SyncExportHandler implements ExportHandler{

    @Override
    public Object handle(ProceedingJoinPoint joinPoint, HttpServletResponse response) throws Throwable {

        String fileName = "数据导出";
        //回调信息
        Object target = joinPoint.getTarget();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        SupportExcelExport annotation = method.getAnnotation(SupportExcelExport.class);
        //基本注解参数校验
        validateAnnotation(annotation,args);
        // 获取注解中的字段值
        String exportFileName = annotation.exportFileName();
        String exportFileMethod = annotation.exportFileNameMethod();
        if(StringUtils.isEmpty(exportFileName) && StringUtils.isEmpty(exportFileMethod)){
            throw new BizException("export excel must assign exportFileName or exportFileNameMethod");
        }
        if(!StringUtils.isEmpty(exportFileName)){
            fileName = exportFileName + "_" + System.currentTimeMillis();
        }else {
            fileName = (String) target.getClass().getMethod(exportFileMethod)
                    .invoke(target);
        }

        String excelFileName = URLEncoder.encode(fileName, "UTF-8");


        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + excelFileName + ".xlsx");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(ExcelWriter writer = EasyExcel.write(baos).build()){
            ExcelExportParse excelExportParse = new ExcelExportParse(target,method,args,writer,null);
            excelExportParse.writer();
        } catch (Exception e){
            log.error("easy export 导出Excel异常",e);
        }

        try (ServletOutputStream out = response.getOutputStream()) {
            baos.writeTo(out);
            out.flush();
        } catch (Exception e){
            log.error("同步导出excel异常，建议使用异步方式");
            log.error("easy export 导出Excel返回异常",e);
        }

        // 由于直接导出操作，不需要返回值
        return null;
    }

    private void validateAnnotation(SupportExcelExport annotation,Object[] args){
        if(annotation == null){
            throw new BizException("export excel must have annotation SupportExcelExport");
        }
        boolean enablePaging = annotation.enablePaging();
        Optional<Object> optional = Arrays.stream(args).filter(PageParam.class::isInstance).findFirst();
        if(enablePaging && optional.isEmpty()){
            throw new BizException("your export excel enable paging,require com.hedgie.component.util.spring.easyexcelexport.PageParam");
        }
    }
}
