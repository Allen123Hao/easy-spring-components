package com.allen.component.easylog;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.allen.component.common.exception.BizException;
import com.allen.component.common.global.GlobalContext;
import com.allen.component.common.global.GlobalVariables;
import com.allen.component.common.util.IdUtil;
import com.allen.component.easyexport.SupportExcelExport;
import com.googlecode.aviator.AviatorEvaluator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 13:47
 */
@Slf4j
public class OperationLogHandler {

    private EasyLogDAO easyLogDAO;

    public OperationLogHandler(){
        EasyLogDAO dao = SpringUtil.getBean("easyLogDAO",EasyLogDAO.class);
        if(dao == null){
            throw new BizException("easy log miss easyLogDAO bean,please check db config");
        }
        this.easyLogDAO = dao;
    }

    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable{

        Object result = null;
        UserOperationLog userOperationLog = initOperationLog(joinPoint);
        try {
            beforeHandle(userOperationLog);

            result = joinPoint.proceed();

            afterHandle(userOperationLog);
            return result;
        } catch (Throwable e) {
            log.warn("easy log record exception,e:{}",e.getMessage());
            exceptionHandle(userOperationLog,new Exception(e));
            throw e;
        }
    }

    protected void beforeHandle(UserOperationLog userOperationLog){

        easyLogDAO.save(userOperationLog);
    }

    protected void afterHandle(UserOperationLog userOperationLog){
        String content = userOperationLog.getContent();
        userOperationLog.setContent(parseContent(content));
        userOperationLog.setStatus((byte)Status.SUCCESS.getCode());
        userOperationLog.setEndAt(LocalDateTime.now());
        long duration = LocalDateTimeUtil.between(userOperationLog.getStartAt(),userOperationLog.getEndAt(), ChronoUnit.MILLIS);
        userOperationLog.setDuration(duration);
        easyLogDAO.update(userOperationLog);
    }

    protected void exceptionHandle(UserOperationLog userOperationLog,Exception ex){
        userOperationLog.setStatus((byte)Status.FAIL.getCode());
        userOperationLog.setEndAt(LocalDateTime.now());
        long duration = LocalDateTimeUtil.between(userOperationLog.getStartAt(),userOperationLog.getEndAt(), ChronoUnit.MILLIS);
        userOperationLog.setDuration(duration);
        HashMap<String,String> meta = new HashMap<>();
        meta.put("exception",ex.getMessage());
        userOperationLog.setMeta(JSONUtil.toJsonStr(meta));
        easyLogDAO.update(userOperationLog);
    }

    protected UserOperationLog initOperationLog(ProceedingJoinPoint joinPoint){
        Object targe = joinPoint.getTarget();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUuid(IdUtil.retrieveUUID());

        GlobalVariables globalVariables = GlobalContext.getGlobalVariables();
        if(globalVariables == null){
            throw new BizException("easy log can not get globalVariables");
        }
        String userUuid = globalVariables.getUserUuid();
        if(StringUtils.isBlank(userUuid)){
            throw new BizException("easy log can not get userUuid");
        }
        userOperationLog.setUserUuid(userUuid);
        userOperationLog.setTraceId(globalVariables.getTraceId());

        OperationLog operationLog = method.getDeclaredAnnotation(OperationLog.class);
        parseOperationLogAnnotation(userOperationLog,operationLog);
        //content先使用原值，后置处理才能解析表达式
        userOperationLog.setContent(operationLog.content());

        //针对SupportExcelExport注解导出操作重置operationType
        resetOperationType(userOperationLog,method);

        if(StringUtils.isBlank(operationLog.apiName())){
            userOperationLog.setApiName(parseApiName(targe,method));
        }
        userOperationLog.setApiUri(parseApiUri(method));
        userOperationLog.setParams(parseParams(args));
        userOperationLog.setStatus((byte)Status.DEFAULT.getCode());
        userOperationLog.setStartAt(LocalDateTime.now());
        userOperationLog.setCreatedAt(LocalDateTime.now());
        return userOperationLog;
    }

    private void parseOperationLogAnnotation(UserOperationLog userOperationLog,OperationLog operationLog){
        String operationType = operationLog.operationType();
        if(StringUtils.isBlank(operationType)){
            throw new BizException("easy log must assign operationType");
        }
        String serviceName = operationLog.serviceName();
        String moduleName = operationLog.moduleName();
        String apiName = operationLog.apiName();

        EasyLogProperties easyLogProperties = SpringUtil.getBean("easyLogProperties",EasyLogProperties.class);
        EasyLogProperties.DefaultConfig defaultConfig = easyLogProperties.getDefaultConfig();
        if(StringUtils.isBlank(serviceName) && StringUtils.isBlank(defaultConfig.getServiceName())){
            throw new BizException("easy log must assign serviceName or config default value");
        }
        if(StringUtils.isBlank(moduleName) && StringUtils.isBlank(defaultConfig.getModuleName())){
            throw new BizException("easy log must assign moduleName or config default value");
        }
        serviceName = StringUtils.isBlank(serviceName)? defaultConfig.getServiceName() : serviceName;
        moduleName = StringUtils.isBlank(moduleName)? defaultConfig.getModuleName() : moduleName;

        userOperationLog.setOperationType(operationType);
        userOperationLog.setServiceName(serviceName);
        userOperationLog.setModuleName(moduleName);
        userOperationLog.setApiName(apiName);
    }

    private void resetOperationType(UserOperationLog userOperationLog,Method method){

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //dubbo调用服务
        if(requestAttributes == null){
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if(method.isAnnotationPresent(SupportExcelExport.class)){
            SupportExcelExport supportExcelExport = method.getDeclaredAnnotation(SupportExcelExport.class);
            // 检查请求中是否包含export=true
            String export = request.getParameter("export");
            if("true".equals(export)){
                userOperationLog.setOperationType("export");
            }
        }
    }


    private String parseApiName(Object targe,Method method){
        String className = targe.getClass().getName();
        String methodName = method.getName();
        return String.join("#",className,methodName);
    }

    private String parseApiUri(Method method){
        String apiUri = null;
        if(method.isAnnotationPresent(GetMapping.class)){
            GetMapping mapping = method.getAnnotation(GetMapping.class);
            apiUri = String.join(",",mapping.value());
        }
        if(method.isAnnotationPresent(PostMapping.class)){
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            apiUri = String.join(",",mapping.value());
        }
        if(method.isAnnotationPresent(PutMapping.class)){
            PutMapping mapping = method.getAnnotation(PutMapping.class);
            apiUri = String.join(",",mapping.value());
        }
        if(method.isAnnotationPresent(PatchMapping.class)){
            PatchMapping mapping = method.getAnnotation(PatchMapping.class);
            apiUri = String.join(",",mapping.value());
        }
        if(method.isAnnotationPresent(DeleteMapping.class)){
            DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
            apiUri = String.join(",",mapping.value());
        }
        if(method.isAnnotationPresent(RequestMapping.class)){
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            apiUri = String.join(",",mapping.value());
        }
        //使用dubbo调用
        if(StringUtils.isBlank(apiUri)){
            GlobalVariables globalVariables = GlobalContext.getGlobalVariables();
            String restfulUrl = globalVariables.getMetaStrValue("restfulurl");
            String dubbo = globalVariables.getMetaStrValue("dubbo");
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOnce("restfulUrl",restfulUrl);
            jsonObject.putOnce("dubbo",dubbo);
            apiUri = JSONUtil.toJsonStr(jsonObject);
        }
        return apiUri;

    }

    private String parseParams(Object[] args){
        StringBuilder params = new StringBuilder();
        for (int i= 0;i<args.length;i++) {
            String key = "arg"+i;
            Object value = args[i];
            try {
                params.append(key).append("=").append(JSONUtil.toJsonStr(value)).append("; ");
            } catch (Exception e) {
                //异常情况直接转换为string
                log.warn("easy log parse params exception:{}",e.getMessage());
                params.append(key).append("=").append(value.toString()).append("; ");
            }
        }
        return params.toString();
    }

    private String parseContent(String content){
        if(StringUtils.isBlank(content)){
            return content;
        }
        Map<String,Object> meta = GlobalContext.getGlobalVariables().getMeta();
        try {
            Object result = AviatorEvaluator.execute(content,meta);
            return result.toString();
        } catch (Exception e) {
            log.error("easy log parse content error,content:{},please check aviator syntax",content);
        }
        return content;
    }


}
