package com.allen.component.easyexport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/19 11:48
 */
@Slf4j
@Aspect
@Order(2)
@Component
public class ExcelExportAspect {

    @Pointcut("@annotation(SupportExcelExport)")
    public void supportExcelExport() {}

    @Around("supportExcelExport()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("go into excelExportAspect");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        // 检查请求中是否包含export=true
        String export = request.getParameter("export");
        if ("true".equals(export)) {
            // 执行导出逻辑
            return handleExport(joinPoint, response);
        } else {
            // 正常执行业务逻辑
            return joinPoint.proceed();
        }
    }

    private Object handleExport(ProceedingJoinPoint joinPoint, HttpServletResponse response) throws Throwable {

        //回调信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SupportExcelExport annotation = method.getAnnotation(SupportExcelExport.class);
        ExportExecuteType exportExecuteType = annotation.exportExecuteType();
        ExportHandler exportHandler;
        if(exportExecuteType == ExportExecuteType.SYNC){
            exportHandler = new SyncExportHandler();
        }else {
            exportHandler = new AsyncExportHandler();
        }
        return exportHandler.handle(joinPoint, response);
    }

}
