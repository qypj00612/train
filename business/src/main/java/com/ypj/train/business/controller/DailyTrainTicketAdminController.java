package com.ypj.train.business.controller;

import com.ypj.train.business.req.DailyTrainTicketQueryReq;
import com.ypj.train.business.resp.DailyTrainTicketQueryResp;
import com.ypj.train.business.service.DailyTrainTicketService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    private final DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> resp = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(resp);
    }
}
