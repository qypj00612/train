package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.TrainSeatQueryReq;
import com.ypj.train.business.resp.TrainSeatQueryResp;
import com.ypj.train.business.service.TrainSeatService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/train-seat")
@RequiredArgsConstructor
public class TrainSeatAdminController {

    private final TrainSeatService trainSeatService;

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(TrainSeatQueryReq req) {
        PageResp<TrainSeatQueryResp> pageResp = trainSeatService.queryList(req);
        return new CommonResp<>(pageResp);
    }
}
