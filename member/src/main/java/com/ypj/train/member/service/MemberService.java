package com.ypj.train.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.member.domain.Member;
import com.ypj.train.member.req.MemberRegisterReq;

/**
* @author Ypj
* @description 针对表【member(会员)】的数据库操作Service
* @createDate 2025-11-13 19:52:24
*/
public interface MemberService extends IService<Member> {

    long register(MemberRegisterReq req);
}
