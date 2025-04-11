package com.allen.component.easyexport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SseManager 管理 SseEmitter 的申请和释放。
 * 使用单例模式确保只有一个 SseManager 实例。
 * 
 * @version 1.1
 * @since 2024/9/19 16:37
 */
@Slf4j
public class SseManager {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private static volatile SseManager instance;
    
    private SseManager() {
        // 私有构造函数
    }
    
    public static SseManager getInstance() {
        if (instance == null) {
            synchronized (SseManager.class) {
                if (instance == null) {
                    instance = new SseManager();
                }
            }
        }
        return instance;
    }

    /**
     * 申请一个新的 SseEmitter 并存储在 emitters 中
     * 
     * @param taskId 任务ID
     * @return 新的 SseEmitter
     */
    public SseEmitter createEmitter(String taskId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(taskId, emitter);


        emitter.onCompletion(() -> {
            log.info("SseEmitter for task {} completed", taskId);
            emitters.remove(taskId);
        });
        emitter.onTimeout(() -> {
            log.info("SseEmitter for task {} timed out", taskId);
            emitters.remove(taskId);
        });
        emitter.onError(throwable -> {
            log.error("SseEmitter for task {} encountered an error: {}", taskId, throwable.getMessage());
            emitters.remove(taskId);
        });

        return emitter;
    }

    /**
     * 根据任务ID获取 SseEmitter
     * 
     * @param taskId 任务ID
     * @return 对应的 SseEmitter
     */
    public SseEmitter getEmitter(String taskId) {
        return emitters.get(taskId);
    }

    /**
     * 完成或者一次释放 SseEmitter
     * 
     * @param taskId 任务ID
     */
    public void completeEmitter(String taskId) {
        SseEmitter emitter = emitters.remove(taskId);
        if (emitter != null) {
            emitter.complete();
        }
    }
}
