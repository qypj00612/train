package com.ypj.train.business.controller.admin;

import com.ypj.train.business.req.StationQueryReq;
import com.ypj.train.business.req.StationSaveReq;
import com.ypj.train.business.resp.StationQueryResp;
import com.ypj.train.business.service.StationService;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("admin/station")
@RequiredArgsConstructor
public class StationAdminController {

    private final StationService stationService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req){
        stationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req){
        PageResp<StationQueryResp> pageResp = stationService.queryList(req);
        return new CommonResp<>(pageResp);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        stationService.removeById(id);
        return new CommonResp<>();
    }

    @GetMapping("query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
