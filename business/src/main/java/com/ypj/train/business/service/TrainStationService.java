package com.ypj.train.business.service;

import com.ypj.train.business.domain.TrainStation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.TrainStationQueryReq;
import com.ypj.train.business.req.TrainStationSaveReq;
import com.ypj.train.business.resp.TrainStationResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【train_station(火车车站)】的数据库操作Service
* @createDate 2025-11-23 18:49:52
*/
public interface TrainStationService extends IService<TrainStation> {

    PageResp<TrainStationResp> queryTrainStationList(TrainStationQueryReq req);

    void save(TrainStationSaveReq req);
}
