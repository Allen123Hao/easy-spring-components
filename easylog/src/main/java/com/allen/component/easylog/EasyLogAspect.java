package com.allen.component.easylog;

import com.allen.component.common.global.GlobalContext;
import com.allen.component.common.global.GlobalVariables;
import com.allen.component.easyexport.SupportExcelExport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/7 10:56
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class EasyLogAspect {

    // 定义Pointcut，指定拦截的方法
    @Pointcut("@annotation(OperationLog)")
    public void operationLog() {}

    @Around("operationLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("go into easyLogAspect");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        GlobalVariables globalVariables = GlobalContext.getGlobalVariables();
        if(StringUtils.isBlank(globalVariables.getUserUuid())){
            log.warn("easy log can not get userUuid,not log user operation");
            return joinPoint.proceed();
        }

        //导出功能的特殊处理，不是真正的导出，配的是export，则不记录日志，针对query不记录日志的情况
        if(!isExportRequest(method)){
            OperationLog operationLog = method.getDeclaredAnnotation(OperationLog.class);
            String operationType = operationLog.operationType();
            if(operationType.contains("export") || operationType.contains("Export")
                    || operationType.contains("EXPORT")){
                return joinPoint.proceed();
            }
        }

        OperationLogHandler handler = new OperationLogHandler();
        return handler.handle(joinPoint);

    }

    private boolean isExportRequest(Method method){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //dubbo调用服务
        if(requestAttributes == null){
            return false;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if(method.isAnnotationPresent(SupportExcelExport.class)){
            SupportExcelExport supportExcelExport = method.getDeclaredAnnotation(SupportExcelExport.class);
            // 检查请求中是否包含export=true
            String export = request.getParameter("export");
            if("true".equals(export)){
                return true;
            }
        }
        return false;
    }
}
