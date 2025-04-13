package com.allen.component.pulsar;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/3/28 11:28
 */
public interface CustomerConsumer {

    void receive(DomainMessage event);
}
