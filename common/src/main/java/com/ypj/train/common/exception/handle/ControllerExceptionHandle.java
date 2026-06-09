package com.ypj.train.common.exception.handle;

import cn.hutool.core.util.StrUtil;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
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
    public CommonResp<String> handleException(Exception e) throws Exception {
        log.error("出现系统错误", e);
        // 若是全局事务里出现异常，就不需要包装返回值，将异常抛给调用方，让事务回滚
        // 否则会认为本次事务是成功的，因为接口返回码是200，会认为本次调用是成功的
        if(StrUtil.isNotEmpty(RootContext.getXID())){
            throw e;
        }
        CommonResp<String> objectCommonResp = new CommonResp<>();
        objectCommonResp.setMessage("系统错误");
        objectCommonResp.setSuccess(false);
        return objectCommonResp;
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp<String> handleBusinessException(BusinessException e) {
        log.error("出现业务异常:{}",e.getExceptionEnum().getDesc());
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
