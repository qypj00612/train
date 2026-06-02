package com.ypj.train.common.exception;

import com.ypj.train.common.exception.enums.BusinessExceptionEnum;

/**
 * 自定义业务异常
 */
public class BusinessException extends RuntimeException {

    private final BusinessExceptionEnum exceptionEnum;

    public BusinessException(BusinessExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    public BusinessExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
