package com.allen.component.pulsar;

import com.allen.component.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
public class PulsarBeanPostProcessor implements BeanPostProcessor {

    private final HashMap<String,Object> CONSUMER_HANDLERS = new HashMap<>();

    private final HashMap<String,Object> PRODUCER_HANDLERS = new HashMap<>();

    @Autowired
    private PulsarInitializer pulsarEventBus;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(ConsumerHandler.class)) {
            ConsumerHandler consumerHandler = bean.getClass().getDeclaredAnnotation(ConsumerHandler.class);
            Object existBean = CONSUMER_HANDLERS.get(consumerHandler.name());
            if(existBean != null){
                log.error("存在重复的消费者名称，name:{},bean1:{},bean2:{}",
                        consumerHandler.name(),bean.getClass().getName(),existBean.getClass().getName());
                throw new BizException("存在重复的消费者名称，name:"+consumerHandler.name());
            }
            CONSUMER_HANDLERS.put(consumerHandler.name(),bean);
            pulsarEventBus.initializeCustomer(bean);
        }
        if(bean.getClass().isAnnotationPresent(ProducerHandler.class)){
            ProducerHandler producerHandler = bean.getClass().getDeclaredAnnotation(ProducerHandler.class);
            Object existBean = PRODUCER_HANDLERS.get(producerHandler.name());
            if(existBean != null){
                log.error("存在重复的生产者名称，name:{},bean1:{},bean2",
                        producerHandler.name(),bean.getClass().getName(),existBean.getClass().getName());
                throw new BizException("存在重复的生产者名称，name:"+producerHandler.name());
            }
            PRODUCER_HANDLERS.put(producerHandler.name(),bean);
            pulsarEventBus.initializeProducer(bean);
        }
        return bean;
    }

}
