package com.ypj.train.business.controller;

import com.ypj.train.business.req.TrainQueryReq;
import com.ypj.train.business.resp.TrainQueryResp;
import com.ypj.train.business.service.TrainService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("business/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> pageResp = trainService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @GetMapping("query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

}
