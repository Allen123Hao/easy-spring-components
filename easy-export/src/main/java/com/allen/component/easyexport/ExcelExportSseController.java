package com.allen.component.easyexport;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/9/14 15:00
 */
@RestController
@RequestMapping("/api/excel-export")
public class ExcelExportSseController {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/progress/{taskId}")
    public SseEmitter getExportProgress(@PathVariable String taskId) {
        SseManager sseManager = SseManager.getInstance();
        return sseManager.getEmitter(taskId);
    }

    public SseEmitter getEmitter(String taskId) {
        return emitters.get(taskId);
    }
}
