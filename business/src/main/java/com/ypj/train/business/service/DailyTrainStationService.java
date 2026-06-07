package com.ypj.train.business.service;

import com.ypj.train.business.domain.DailyTrainStation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.DailyTrainStationQueryReq;
import com.ypj.train.business.req.DailyTrainStationSaveReq;
import com.ypj.train.business.resp.DailyTrainStationQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【daily_train_station(每日车站)】的数据库操作Service
* @createDate 2025-11-26 21:17:40
*/
public interface DailyTrainStationService extends IService<DailyTrainStation> {

    PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req);

    void save(DailyTrainStationSaveReq req);
}
