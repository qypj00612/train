package com.ypj.train.business.service;

import com.ypj.train.business.domain.DailyTrainCarriage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.DailyTrainCarriageQueryReq;
import com.ypj.train.business.req.DailyTrainCarriageSaveReq;
import com.ypj.train.business.resp.DailyTrainCarriageResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【daily_train_carriage(每日车厢)】的数据库操作Service
* @createDate 2025-11-26 21:57:27
*/
public interface DailyTrainCarriageService extends IService<DailyTrainCarriage> {

    PageResp<DailyTrainCarriageResp> queryList(DailyTrainCarriageQueryReq req);

    void save(DailyTrainCarriageSaveReq req);
}
