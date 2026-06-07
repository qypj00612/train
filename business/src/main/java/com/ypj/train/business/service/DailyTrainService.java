package com.ypj.train.business.service;

import com.ypj.train.business.domain.DailyTrain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.DailyTrainQueryReq;
import com.ypj.train.business.req.DailyTrainSaveReq;
import com.ypj.train.business.resp.DailyTrainQueryResp;
import com.ypj.train.common.resp.PageResp;

import java.util.Date;

/**
* @author Ypj
* @description 针对表【daily_train(每日车次)】的数据库操作Service
* @createDate 2025-11-26 20:01:18
*/
public interface DailyTrainService extends IService<DailyTrain> {

    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req);

    void save(DailyTrainSaveReq req);

    void genDaily(Date data);
}
