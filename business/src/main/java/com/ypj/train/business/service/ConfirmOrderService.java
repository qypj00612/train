package com.ypj.train.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.domain.ConfirmOrder;
import com.ypj.train.business.domain.ConfirmOrderMQDTO;
import com.ypj.train.business.req.ConfirmOrderQueryReq;
import com.ypj.train.business.resp.ConfirmOrderQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【confirm_order(确认订单)】的数据库操作Service
* @createDate 2025-12-02 21:48:22
*/
public interface ConfirmOrderService extends IService<ConfirmOrder> {

    PageResp<ConfirmOrderQueryResp> query(ConfirmOrderQueryReq req);

    void doConfirm(ConfirmOrderMQDTO req);

    Integer queryLineCount(Long id);
}
