package com.ypj.train.business.controller.admin;

import com.ypj.train.api.client.MemberClient;
import com.ypj.train.common.req.TicketQueryReq;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.resp.TicketQueryResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/ticket")
public class TicketAdminController {

    private final MemberClient memberClient;

    @GetMapping("query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(TicketQueryReq req) {
        return memberClient.queryList(req.getPage(),req.getPageSize());
    }

}
