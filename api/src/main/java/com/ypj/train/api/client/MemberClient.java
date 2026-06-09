package com.ypj.train.api.client;

import com.ypj.train.common.domain.TicketSaveReq;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.resp.TicketQueryResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("member")
public interface MemberClient {
    @PostMapping("member/ticket/save")
    CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req);

    @GetMapping("member/ticket/admin/query-list")
    CommonResp<PageResp<TicketQueryResp>> queryList(@RequestParam Integer page, @RequestParam Integer pageSize);
}
