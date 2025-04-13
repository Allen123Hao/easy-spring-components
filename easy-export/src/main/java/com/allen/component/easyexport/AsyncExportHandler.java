package com.allen.component.easyexport;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.allen.component.common.exception.BizException;
import com.allen.component.common.oss.Config;
import com.allen.component.common.oss.Storage;
import com.allen.component.common.util.IdUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/9/14 15:49
 */
@Slf4j
public class AsyncExportHandler implements ExportHandler {

    private static final String FILE_EXTENSION = ".xlsx";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8,
            new ThreadFactoryBuilder().setNameFormat("excel-export-thread-%d")
                    .build());


    @Override
    public Object handle(ProceedingJoinPoint joinPoint, HttpServletResponse response) throws Throwable {

        String fileName = "数据导出";
        //回调信息
        Object target = joinPoint.getTarget();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        //为了立刻返回包含taskUuid的结果，分页只查询1条数据
        adjustParams(args,1,1);

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

        String excelFileName = fileName;

        String taskUuid = IdUtil.retrieveUUID();


        SseManager sseManager = SseManager.getInstance();
        SseEmitter emitter = sseManager.createEmitter(taskUuid);

        log.info("sse emitter created,can view /api/excel-export/progress/{}", taskUuid);

        //防止任务太快，保证前端可以查看sse
        for (int i = 0; i < 3; i++) {
            try {
                emitter.send(SseEmitter.event().name("progress").data("0.00"));
                Thread.sleep(500);
            } catch (IOException e) {
                log.error("easy export sse send data error");
            } catch (InterruptedException e) {
                log.error("easy export InterruptedException");
                Thread.currentThread().interrupt();
            }
        }

        // 定期发送心跳消息
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                log.error("Error sending heartbeat", e);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        Object object = joinPoint.proceed();
        if (object instanceof ExcelExportResult result) {
            result.setTaskUuid(taskUuid);
        }

        executorService.submit(() -> {
            log.info("start write excel");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ExcelWriter writer = EasyExcel.write(bos).build()){
                ExcelExportParse excelExportParse = new ExcelExportParse(target, method, args, writer, emitter);
                excelExportParse.writer();
                // 将异步生成的excel，上传到OSS
                ExcelOssProvider excelOssProvider = SpringUtil.getBean(ExcelOssProvider.class);
                Storage storage = excelOssProvider.getPgcStorage();
                String path = "easyexport/excels/" + excelFileName + FILE_EXTENSION;
                storage.writeBytes(path, bos.toByteArray(), new Config(Map.of("mime", "application/octet-stream")));
                String ossUrl = storage.publicUrl(path);
                emitter.send(SseEmitter.event().name("result").data(ossUrl));
            } catch (Exception e) {
                log.error("easy export 导出Excel异常", e);
            } finally {
                sseManager.completeEmitter(taskUuid);
                scheduler.shutdown();
            }
        });

        return object;
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

    private void adjustParams(Object[] args,int pageNo,int pageSize){
        for (Object arg : args) {
            if (arg instanceof PageParam pageParam) {
                pageParam.setPageNo(pageNo);
                pageParam.setPageSize(pageSize);
            }
        }
    }
}
