package com.ypj.train.business.service;

import com.ypj.train.business.domain.DailyTrainTicket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.DailyTrainTicketQueryReq;
import com.ypj.train.business.resp.DailyTrainTicketQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【daily_train_ticket(余票信息)】的数据库操作Service
* @createDate 2025-12-01 18:26:49
*/
public interface DailyTrainTicketService extends IService<DailyTrainTicket> {

    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);
}
