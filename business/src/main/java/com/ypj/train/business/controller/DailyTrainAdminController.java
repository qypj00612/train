package com.ypj.train.business.controller;

import com.ypj.train.business.req.DailyTrainQueryReq;
import com.ypj.train.business.req.DailyTrainSaveReq;
import com.ypj.train.business.resp.DailyTrainQueryResp;
import com.ypj.train.business.service.DailyTrainService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/daily-train")
@RequiredArgsConstructor
public class DailyTrainAdminController {

    private final DailyTrainService dailyTrainService;

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(DailyTrainQueryReq req) {
        PageResp<DailyTrainQueryResp> resp = dailyTrainService.queryList(req);
        return new CommonResp<>(resp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
        dailyTrainService.save(req);
        return new CommonResp<>();
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainService.removeById(id);
        return new CommonResp<>();
    }
}
