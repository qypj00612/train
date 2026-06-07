package com.ypj.train.business.controller;

import com.ypj.train.business.req.DailyTrainCarriageQueryReq;
import com.ypj.train.business.req.DailyTrainCarriageSaveReq;
import com.ypj.train.business.resp.DailyTrainCarriageResp;
import com.ypj.train.business.service.DailyTrainCarriageService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

    private final DailyTrainCarriageService dailyTrainCarriageService;

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainCarriageResp>> queryList(DailyTrainCarriageQueryReq req) {
        PageResp<DailyTrainCarriageResp> pageResp = dailyTrainCarriageService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req){
        dailyTrainCarriageService.save(req);
        return new CommonResp<>();
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        dailyTrainCarriageService.removeById(id);
        return new CommonResp<>();
    }

}
