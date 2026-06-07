package com.ypj.train.business.service;

import com.ypj.train.business.domain.DailyTrainSeat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.DailyTrainSeatQueryReq;
import com.ypj.train.business.resp.DailyTrainSeatQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【daily_train_seat(每日座位)】的数据库操作Service
* @createDate 2025-11-27 19:37:18
*/
public interface DailyTrainSeatService extends IService<DailyTrainSeat> {

    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);
}
