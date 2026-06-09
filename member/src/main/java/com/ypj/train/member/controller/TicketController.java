package com.ypj.train.member.controller;

import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.member.req.TicketSaveReq;
import com.ypj.train.member.resp.TicketQueryResp;
import com.ypj.train.member.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("member/ticket")
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
    public CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req) {
        ticketService.save(req);
        return new CommonResp<>();
    }

}
