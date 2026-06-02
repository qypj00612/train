package com.ypj.train.common.exception.handle;

import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.resp.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp<String> handleException(Exception e) {
        log.error("系统错误", e);
        CommonResp<String> objectCommonResp = new CommonResp<>();
        objectCommonResp.setMessage("系统错误");
        objectCommonResp.setSuccess(false);
        return objectCommonResp;
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp<String> handleBusinessException(BusinessException e) {
        log.error("业务异常:{}",e.getExceptionEnum().getDesc());
        CommonResp<String> objectCommonResp = new CommonResp<>();
        objectCommonResp.setMessage(e.getExceptionEnum().getDesc());
        objectCommonResp.setSuccess(false);
        return objectCommonResp;
    }

    /**
     * 集成校验
     * @param e 校验错误
     * @return 错误信息
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp<String> handleBindException(BindException e) {
        log.error("业务异常:{}",e.getBindingResult().getFieldError().getDefaultMessage());
        CommonResp<String> objectCommonResp = new CommonResp<>();
        objectCommonResp.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
        objectCommonResp.setSuccess(false);
        return objectCommonResp;
    }
}
