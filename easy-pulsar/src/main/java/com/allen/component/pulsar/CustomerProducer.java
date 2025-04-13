package com.allen.component.pulsar;

import com.allen.component.common.exception.BizException;
import com.allen.component.common.util.GsonUtil;
import com.allen.component.common.util.SpringUtils;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 11:28
 */
public interface CustomerProducer {

    /**
     * 获取pulsar的producer
     * @return
     */
    default Producer<String> getProducer(){
        Class clazz = this.getClass();
        boolean isPresent = clazz.isAnnotationPresent(ProducerHandler.class);
        if(!isPresent){
            throw new BizException("生产者注解未找到ProducerHandler注解,className:"+clazz.getSimpleName());
        }
        ProducerHandler producerHandler = (ProducerHandler) clazz.getDeclaredAnnotation(ProducerHandler.class);
        String name = producerHandler.name();
        return (Producer<String>) SpringUtils.getBean(name);
    }

    void send(DomainMessage event) throws PulsarClientException;

    /**
     * 序列化必须使用GsonUtil，统一日期序列化格式，所以推荐使用serialize方法序列化
     * @param message
     * @return
     */
    default String serialize(DomainMessage message){
        return GsonUtil.toJson(message);
    }
}
