package com.allen.component.common.exception;

import java.io.Serializable;

public class BizException extends BaseException implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_ERR_CODE = "BIZ_ERROR";

    public BizException(String errMessage) {
        super(DEFAULT_ERR_CODE, errMessage);
    }

    public BizException(String errCode, String errMessage) {
        super(errCode, errMessage);
    }

    public BizException(String errCode, String errMessage, Object... args) {
        super(errCode, errMessage, args);
    }

    public BizException(String errMessage, Throwable e) {
        super(DEFAULT_ERR_CODE, errMessage, e);
    }

    public BizException(String errMessage, Throwable e, Object... args) {
        super(DEFAULT_ERR_CODE, errMessage, e, args);
    }

    public BizException(String errorCode, String errMessage, Throwable e) {
        super(errorCode, errMessage, e);
    }

    public BizException(String errorCode, String errMessage, Throwable e, Object... args) {
        super(errorCode, errMessage, e, args);
    }

}