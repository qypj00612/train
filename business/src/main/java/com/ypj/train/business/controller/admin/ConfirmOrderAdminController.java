package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.ConfirmOrderQueryReq;
import com.ypj.train.business.resp.ConfirmOrderQueryResp;
import com.ypj.train.business.service.ConfirmOrderService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/confirm-order")
public class ConfirmOrderAdminController {

    private final ConfirmOrderService confirmOrderService;

    @GetMapping("query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> query(ConfirmOrderQueryReq req) {
        PageResp<ConfirmOrderQueryResp> resp = confirmOrderService.query(req);
        return new CommonResp<>(resp);
    }
}
