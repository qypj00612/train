package com.ypj.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ypj.train.business.req.ConfirmOrderDoReq;
import com.ypj.train.business.service.ConfirmOrderService;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("business/confirm-order")
@Slf4j
public class ConfirmOrderController {

    private final ConfirmOrderService confirmOrderService;

    @PostMapping("do")
    @SentinelResource(value = "confirm-order", blockHandler = "doConfirmBlock")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }

    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException blockException) {
        log.info("购票请求被限流: {}", req);
        throw new BusinessException(BusinessExceptionEnum.TICKET_EXCEPTION_FLOW);
    }

    @GetMapping("hello")
    public CommonResp<String> hello() {
        return new CommonResp<>("hello");
    }
}
