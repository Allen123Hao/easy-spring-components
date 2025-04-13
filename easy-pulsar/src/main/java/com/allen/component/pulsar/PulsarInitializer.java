package com.allen.component.pulsar;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.allen.component.common.exception.BizException;
import com.allen.component.common.util.GsonUtil;
import com.allen.component.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 11:29
 */
@Service
@Slf4j
public class PulsarInitializer {

    private final String TOPIC_FORMAT = "persistent://hedgie/{}/{}";

    private final Set<String> CONSUMER_SUB_NAMES = new HashSet<>();

    @Autowired
    private PulsarClient pulsarClient;

    @Autowired
    private ProducerBeanFactory producerBeanInitializer;

    public void initializeCustomer(Object listener) {
        boolean isConsumerHandler = listener.getClass().isAnnotationPresent(ConsumerHandler.class);
        if (!isConsumerHandler) {
            throw new BizException("定义消费者异常，缺少ConsumerHandler注解");
        }
        ConsumerHandler consumerHandler = listener.getClass().getDeclaredAnnotation(ConsumerHandler.class);
        String consumerName = consumerHandler.name();
        String topic = consumerHandler.topic();
        String env = SpringUtil.getProperty("common.env");
        topic = StrUtil.format(TOPIC_FORMAT, env, topic);
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscription.class)) {
                Subscription subscription = method.getDeclaredAnnotation(Subscription.class);
                String subscriptionName = consumerName + "-sub";
                if (StringUtils.isNotEmpty(subscription.name())) {
                    subscriptionName = subscription.name();
                }
                if(CONSUMER_SUB_NAMES.contains(subscriptionName)){
                    log.error("消费者订阅的名称重复,subscriptionName:{}",subscriptionName);
                    throw new BizException("消费者订阅的名称重复,subscriptionName:"+subscriptionName);
                }
                CONSUMER_SUB_NAMES.add(subscriptionName);
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    Class<?> eventType = parameterTypes[0];
                    try {
                        pulsarClient.newConsumer(Schema.STRING)
                                .consumerName(consumerName)
                                .topic(topic)
                                .subscriptionName(subscriptionName)
                                .subscriptionType(SubscriptionType.Shared)
                                .messageListener((cons, msg) -> {
                                    try {
                                        log.info("PulsarEventBus consumer listener,cons:{},msg:{}",
                                                cons.getClass().getName(),msg);
                                        Object event = GsonUtil.toObject(msg.getValue(), eventType);
                                        method.invoke(listener, event);
                                    } catch (Exception e) {
                                        log.error("pulsar exception: ", e);
                                    } finally {
                                        try {
                                            cons.acknowledge(msg);
                                        } catch (PulsarClientException e) {
                                            log.error("pulsar exception: ", e);
                                        }
                                    }
                                })
                                .negativeAckRedeliveryDelay(30, TimeUnit.SECONDS)
                                .enableRetry(true)
                                .subscribe();
                    } catch (PulsarClientException e) {
                        log.error("pulsar exception: ", e);
                    }
                }
            }
        }
    }


    public void initializeProducer(Object producerHandlerBean) {
        ProducerHandler producerHandler = producerHandlerBean.getClass().getDeclaredAnnotation(ProducerHandler.class);
        String name = producerHandler.name();
        String topic = producerHandler.topic();
        String env = SpringUtil.getProperty("common.env");
        topic = StrUtil.format(TOPIC_FORMAT, env, topic);
        //pulsar要求多个节点不能重复
        String producerName = String.join("_", name, IdUtil.retrieveUUIDWithNoHyphenated());
        try {
            Producer<String> producer = pulsarClient.newProducer(Schema.STRING)
                    .producerName(producerName)
                    .topic(topic)
                    .create();
            producerBeanInitializer.registerProducerAsSingleton(name, producer);
        } catch (PulsarClientException e) {
            log.error("创建pulsar的producer异常,producerName:{}", producerName, e);
            throw new BizException("创建pulsar的producer异常");
        }
    }

    public void close() throws PulsarClientException {
        pulsarClient.close();
    }

}
