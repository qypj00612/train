package com.ypj.train.member.controller;

import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.member.req.PassengerQueryReq;
import com.ypj.train.member.req.PassengerSaveReq;
import com.ypj.train.member.resp.PassengerQueryResp;
import com.ypj.train.member.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        passengerService.save(passengerSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryReq passengerQueryReq) {
        passengerQueryReq.setMemberId(MemberContext.getId());
        PageResp<PassengerQueryResp> queryReqPageResp = passengerService.queryList(passengerQueryReq);
        return new CommonResp<>(queryReqPageResp);
    }
}
