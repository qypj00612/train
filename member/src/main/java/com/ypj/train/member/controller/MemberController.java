package com.ypj.train.member.controller;

import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.member.req.MemberLoginReq;
import com.ypj.train.member.req.MemberRegisterReq;
import com.ypj.train.member.req.MemberSendCodeReq;
import com.ypj.train.member.resp.MemberLoginResp;
import com.ypj.train.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("count")
    public CommonResp<Long> count(){
        long count = memberService.count();
        return new CommonResp<>(count);
    }

    @PostMapping("register")
    public CommonResp<Long> register(@Valid @RequestBody MemberRegisterReq mobile){
        long register = memberService.register(mobile);
        return new CommonResp<>(register);
    }

    @PostMapping("send-code")
    public CommonResp<String> sendCode(@Valid @RequestBody MemberSendCodeReq mobile){
        memberService.sendCode(mobile);
        return new CommonResp<>();
    }

    @PostMapping("login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq loginReq){
        MemberLoginResp resp = memberService.login(loginReq);
        return new CommonResp<>(resp);
    }
}
