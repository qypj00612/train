package com.ypj.train.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.member.domain.Ticket;
import com.ypj.train.member.req.TicketSaveReq;
import com.ypj.train.member.resp.TicketQueryResp;

/**
* @author Ypj
* @description 针对表【ticket(车票)】的数据库操作Service
* @createDate 2025-12-10 20:39:16
*/
public interface TicketService extends IService<Ticket> {

    PageResp<TicketQueryResp> queryList(Integer page, Integer pageSize, Long memberId);

    void save(TicketSaveReq req);
}
