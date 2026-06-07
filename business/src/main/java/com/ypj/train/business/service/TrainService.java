package com.ypj.train.business.service;

import com.ypj.train.business.domain.Train;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.TrainQueryReq;
import com.ypj.train.business.req.TrainSaveReq;
import com.ypj.train.business.resp.TrainQueryResp;
import com.ypj.train.common.resp.PageResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【train(车次)】的数据库操作Service
* @createDate 2025-11-20 21:26:12
*/
public interface TrainService extends IService<Train> {

    PageResp<TrainQueryResp> queryList(TrainQueryReq req);

    void save(TrainSaveReq req);

    List<TrainQueryResp> queryAll();

}
