package com.ypj.train.business.controller;

import com.ypj.train.business.req.ConfirmOrderDoReq;
import com.ypj.train.business.service.ConfirmOrderService;
import com.ypj.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("business/confirm-order")
public class ConfirmOrderController {

    private final ConfirmOrderService confirmOrderService;

    @PostMapping("do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }
}
