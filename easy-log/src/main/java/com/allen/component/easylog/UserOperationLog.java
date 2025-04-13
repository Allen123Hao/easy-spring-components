package com.allen.component.easylog;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 11:40
 */
@Data
public class UserOperationLog {

    /**
     * uuid
     */
    private String uuid;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 模块名，单模块服务可与service_name相同
     */
    private String moduleName;

    /**
     * Trace id
     */
    private String traceId;

    /**
     * 操作类型，关联log_operation_type表的operation_type
     */
    private String operationType;

    /**
     * 接口名
     */
    private String apiName;

    /**
     * 接口URI
     */
    private String apiUri;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 修改内容，可选
     */
    private String content;

    /**
     * 调用状态，0-默认，1-成功，2-失败
     */
    private Byte status;

    /**
     * 开始时间
     */
    private LocalDateTime startAt;

    /**
     * 结束时间
     */
    private LocalDateTime endAt;

    /**
     * 耗时，单位毫秒
     */
    private Long duration;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 扩展信息
     */
    private String meta;

}
