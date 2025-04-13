package com.allen.component.example;

import com.allen.component.pulsar.CustomerProducer;
import com.allen.component.pulsar.DomainMessage;
import com.allen.component.pulsar.ProducerHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 13:42
 */
@Slf4j
@ProducerHandler(name="test-producer",topic = "test-msg")
public class MessageProducer implements CustomerProducer {

    public void send(DomainMessage message) throws PulsarClientException {
        Producer<String>  producer = getProducer();
        String msg = serialize(message);
        log.info("发送消息：{}",msg);
        producer.send(msg);
    }
}