package com.ypj.train.business.controller;

import com.ypj.train.business.req.TrainQueryReq;
import com.ypj.train.business.req.TrainSaveReq;
import com.ypj.train.business.resp.TrainQueryResp;
import com.ypj.train.business.service.TrainSeatService;
import com.ypj.train.business.service.TrainService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/train")
@RequiredArgsConstructor
public class TrainAdminController {

    private final TrainService trainService;
    private final TrainSeatService trainSeatService;

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> pageResp = trainService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        trainService.save(req);
        return new CommonResp<>();
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.removeById(id);
        return new CommonResp<>();
    }

    @GetMapping("query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

    @GetMapping("generate-seat/{code}")
    public CommonResp<Object> generateSeat(@PathVariable String code) {
        trainSeatService.generateSeat(code);
        return new CommonResp<>();
    }
}
