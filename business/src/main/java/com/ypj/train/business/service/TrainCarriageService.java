package com.ypj.train.business.service;

import com.ypj.train.business.domain.TrainCarriage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.TrainCarriageQueryReq;
import com.ypj.train.business.req.TrainCarriageSaveReq;
import com.ypj.train.business.resp.TrainCarriageQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【train_carriage(火车车厢)】的数据库操作Service
* @createDate 2025-11-24 19:41:07
*/
public interface TrainCarriageService extends IService<TrainCarriage> {

    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    void save(TrainCarriageSaveReq req);
}
