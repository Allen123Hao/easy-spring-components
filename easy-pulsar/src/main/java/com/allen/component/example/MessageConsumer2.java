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
 * @since 2024/3/29 10:50
 */
@Slf4j
@ConsumerHandler(name = "test-customer2",topic = "test-msg2")
public class MessageConsumer2 implements CustomerConsumer {

    @Override
    @Subscription
    public void receive(DomainMessage eventMessage) {
        log.info("test-customer2接受消息：{}", GsonUtil.toJson(eventMessage));
    }
}
