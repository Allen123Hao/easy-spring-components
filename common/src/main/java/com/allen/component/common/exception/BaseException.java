package com.allen.component.common.exception;

import java.io.Serializable;

public abstract class BaseException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private String errCode;

    private Object[] args; // 参数列表

    public BaseException(String errMessage) {
        super(errMessage);
    }

    public BaseException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
    }

    public BaseException(String errCode, String errMessage, Object... args) {
        super(formatMessage(errMessage));
        this.errCode = errCode;
        this.args = args;
    }

    public BaseException(String errMessage, Throwable e) {
        super(errMessage, e);
    }

    public BaseException(String errCode, String errMessage, Throwable e) {
        super(errMessage, e);
        this.errCode = errCode;
    }

    public BaseException(String errCode, String errMessage, Throwable e, Object... args) {
        super(formatMessage(errMessage), e);
        this.errCode = errCode;
        this.args = args;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    // 格式化消息的工具方法
    protected static String formatMessage(String message, Object... args) {
        return args != null && args.length > 0 ? String.format(message, args) : message;
    }

}
