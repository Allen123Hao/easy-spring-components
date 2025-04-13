package com.allen.component.pulsar;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DomainMessage<T> {

    private String type;

    private T payload;

    private LocalDateTime occurredAt;

}
