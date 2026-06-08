package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.TrainStationQueryReq;
import com.ypj.train.business.req.TrainStationSaveReq;
import com.ypj.train.business.resp.TrainStationResp;
import com.ypj.train.business.service.TrainStationService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/train-station")
public class TrainStationAdminController {

    private final TrainStationService trainStationService;

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainStationResp>> queryTrainStationList(@Valid TrainStationQueryReq req) {
        PageResp<TrainStationResp> resp = trainStationService.queryTrainStationList(req);
        return new CommonResp<>(resp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
        trainStationService.save(req);
        return new CommonResp<>();
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.removeById(id);
        return new CommonResp<>();
    }
}
