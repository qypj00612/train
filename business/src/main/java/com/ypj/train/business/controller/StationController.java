package com.ypj.train.business.controller;

import com.ypj.train.business.req.StationQueryReq;
import com.ypj.train.business.resp.StationQueryResp;
import com.ypj.train.business.service.StationService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("business/station")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping("query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req){
        PageResp<StationQueryResp> pageResp = stationService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @GetMapping("query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
