package com.ypj.train.member.controller;

import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.member.req.MemberRegisterReq;
import com.ypj.train.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CommonResp<Long> register(@Valid MemberRegisterReq mobile){
        long register = memberService.register(mobile);
        return new CommonResp<>(register);
    }
}
