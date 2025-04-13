package com.allen.component.example;

import com.allen.component.common.util.GsonUtil;
import com.allen.component.pulsar.ConsumerHandler;
import com.allen.component.pulsar.CustomerConsumer;
import com.allen.component.pulsar.DomainMessage;
import com.allen.component.pulsar.Subscription;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 14:28
 */
@Slf4j
@ConsumerHandler(name = "test-customer",topic = "test-msg")
public class MessageConsumer implements CustomerConsumer {

    @Override
    @Subscription
    public void receive(DomainMessage eventMessage) {
        log.info("test-customer接受消息：{}", GsonUtil.toJson(eventMessage));
    }
}
