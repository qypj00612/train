package com.ypj.train.member.controller;

import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.member.req.TicketSaveReq;
import com.ypj.train.member.resp.TicketQueryResp;
import com.ypj.train.member.service.TicketService;
import io.seata.core.context.RootContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("member/ticket")
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@RequestParam Integer page, @RequestParam Integer pageSize) {
        PageResp<TicketQueryResp> resp = ticketService.queryList(page, pageSize, MemberContext.getId());
        return new CommonResp<>(resp);
    }

    @GetMapping("admin/query-list")
    CommonResp<PageResp<TicketQueryResp>> adminQueryList(@RequestParam Integer page, @RequestParam Integer pageSize){
        PageResp<TicketQueryResp> resp = ticketService.queryList(page, pageSize,null);
        return new CommonResp<>(resp);
    }

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req) throws Exception {
        log.info("全局事务ID:{}", RootContext.getXID());
        ticketService.save(req);
        return new CommonResp<>();
    }

}
