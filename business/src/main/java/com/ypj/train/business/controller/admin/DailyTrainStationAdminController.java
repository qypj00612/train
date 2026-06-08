package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.DailyTrainStationQueryReq;
import com.ypj.train.business.req.DailyTrainStationSaveReq;
import com.ypj.train.business.resp.DailyTrainStationQueryResp;
import com.ypj.train.business.service.DailyTrainStationService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/daily-train-station")
public class DailyTrainStationAdminController {

    private final DailyTrainStationService dailyTrainStationService;

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(DailyTrainStationQueryReq req) {
        PageResp<DailyTrainStationQueryResp> pageResp = dailyTrainStationService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req) {
        dailyTrainStationService.save(req);
        return new CommonResp<>();
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainStationService.removeById(id);
        return new CommonResp<>();
    }
}
