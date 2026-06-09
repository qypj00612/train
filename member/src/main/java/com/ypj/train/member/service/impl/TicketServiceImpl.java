package com.ypj.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.member.domain.Ticket;
import com.ypj.train.member.mapper.TicketMapper;
import com.ypj.train.member.req.TicketSaveReq;
import com.ypj.train.member.resp.TicketQueryResp;
import com.ypj.train.member.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【ticket(车票)】的数据库操作Service实现
* @createDate 2025-12-10 20:39:16
*/
@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket>
    implements TicketService{

    private final TicketMapper ticketMapper;

    @Override
    public PageResp<TicketQueryResp> queryList(Integer page, Integer pageSize, Long memberId) {
        Page<Ticket> ticketPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Ticket> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(ObjectUtil.isNotNull(memberId)){
            lambdaQueryWrapper.eq(Ticket::getMemberId, MemberContext.getMember().getId());
        }

        ticketMapper.selectPage(ticketPage, lambdaQueryWrapper);
        List<Ticket> records = ticketPage.getRecords();

        List<TicketQueryResp> ticketQueryResps = BeanUtil.copyToList(records, TicketQueryResp.class);
        PageResp<TicketQueryResp> resp = new PageResp<>();
        resp.setTotal(ticketPage.getTotal());
        resp.setRows(ticketQueryResps);

        return resp;
    }

    @Override
    public void save(TicketSaveReq req) {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
    }
}




