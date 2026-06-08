package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.DailyTrainSeatQueryReq;
import com.ypj.train.business.resp.DailyTrainSeatQueryResp;
import com.ypj.train.business.service.DailyTrainSeatService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/daily-train-seat")
public class DailyTrainSeatAdminController {

    private final DailyTrainSeatService dailyTrainSeatService;

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(DailyTrainSeatQueryReq req) {
        PageResp<DailyTrainSeatQueryResp> pageResp = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(pageResp);
    }
}
