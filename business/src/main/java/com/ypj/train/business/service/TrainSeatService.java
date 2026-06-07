package com.ypj.train.business.service;

import com.ypj.train.business.domain.TrainSeat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.TrainSeatQueryReq;
import com.ypj.train.business.resp.TrainSeatQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【train_seat(座位)】的数据库操作Service
* @createDate 2025-11-24 20:26:55
*/
public interface TrainSeatService extends IService<TrainSeat> {

    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    void generateSeat(String code);
}
