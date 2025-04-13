package com.allen.component.pulsar;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen
 * @version 1.0
 * @date 2023/6/8 12:22
 */
@Slf4j
@Configuration
public class PulsarConfig {

    @Value("${pulsar.server.url}")
    private String serverUrl;

    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(serverUrl)
                .build();
    }

}
