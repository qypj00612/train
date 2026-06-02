package com.ypj.train.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.member.domain.Member;
import com.ypj.train.member.mapper.MemberMapper;
import com.ypj.train.member.req.MemberRegisterReq;
import com.ypj.train.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【member(会员)】的数据库操作Service实现
* @createDate 2025-11-13 19:52:24
*/
@RequiredArgsConstructor
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member>
    implements MemberService{

    private final MemberMapper memberMapper;

    @Override
    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<Member>()
                .eq(Member::getMobile, mobile);
        List<Member> members = memberMapper.selectList(queryWrapper);
        if(CollUtil.isNotEmpty(members)){
            throw new RuntimeException("手机号被注册");
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }
}