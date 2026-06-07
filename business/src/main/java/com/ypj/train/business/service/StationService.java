package com.ypj.train.business.service;

import com.ypj.train.business.domain.Station;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.StationQueryReq;
import com.ypj.train.business.req.StationSaveReq;
import com.ypj.train.business.resp.StationQueryResp;
import com.ypj.train.common.resp.PageResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【station(车站)】的数据库操作Service
* @createDate 2025-11-20 20:22:37
*/
public interface StationService extends IService<Station> {

    void save(StationSaveReq req);

    PageResp<StationQueryResp> queryList(StationQueryReq req);

    List<StationQueryResp> queryAll();
}
