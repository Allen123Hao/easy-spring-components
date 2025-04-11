package com.allen.component.easylog;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 17:16
 */
@Getter
@AllArgsConstructor
public enum Status {

    DEFAULT(0),
    SUCCESS(1),
    FAIL(2);

    int code;


}
