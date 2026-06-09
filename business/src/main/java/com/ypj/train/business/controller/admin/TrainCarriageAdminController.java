package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.TrainCarriageQueryReq;
import com.ypj.train.business.req.TrainCarriageSaveReq;
import com.ypj.train.business.resp.TrainCarriageQueryResp;
import com.ypj.train.business.service.TrainCarriageService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/train-carriage")
@RequiredArgsConstructor
public class TrainCarriageAdminController {

    private final TrainCarriageService trainCarriageService;

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        PageResp<TrainCarriageQueryResp> pageResp=trainCarriageService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.save(req);
        return new CommonResp<>();
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.removeById(id);
        return new CommonResp<>();
    }

}
