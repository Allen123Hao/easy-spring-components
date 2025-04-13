package com.allen.component.easyexport;

import com.allen.component.common.oss.AliyunStorage;
import com.allen.component.common.oss.Storage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/9/14 16:41
 */
@Component
@ConditionalOnClass(name = "com.aliyun.oss.OSS")
public class ExcelOssProvider {

    @Value("${ali.oss.server.bucketName}")
    private String bucketName;

    @Value("${ali.oss.server.endpoint}")
    private String endpoint;

    @Value("${ali.oss.server.cdnEndpoint}")
    private String cdnEndpoint;

    @Value("${ali.oss.server.accessKey}")
    private String accessKey;

    @Value("${ali.oss.server.accessSecret}")
    private String accessSecret;


    private AliyunStorage pgcStorage;

    private ExcelOssProvider() {
    }

    @PostConstruct
    public void initStorage() {
        pgcStorage = new AliyunStorage(endpoint, accessKey, accessSecret, bucketName, "https://" + cdnEndpoint);
    }

    public Storage getPgcStorage() {
        return pgcStorage;
    }
}
