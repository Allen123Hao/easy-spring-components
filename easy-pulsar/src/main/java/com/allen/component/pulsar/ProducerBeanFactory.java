package com.allen.component.pulsar;

import org.apache.pulsar.client.api.Producer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 14:02
 */
@Component
public class ProducerBeanFactory implements BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    public void registerProducerAsSingleton(String beanName, Producer<String> producer) {
        beanFactory.registerSingleton(beanName, producer);
    }

}
