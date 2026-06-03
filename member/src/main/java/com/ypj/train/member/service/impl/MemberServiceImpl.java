package com.ypj.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.util.JwtUtil;
import com.ypj.train.common.util.SnowUtil;
import com.ypj.train.member.domain.Member;
import com.ypj.train.member.mapper.MemberMapper;
import com.ypj.train.member.req.MemberLoginReq;
import com.ypj.train.member.req.MemberRegisterReq;
import com.ypj.train.member.req.MemberSendCodeReq;
import com.ypj.train.member.resp.MemberLoginResp;
import com.ypj.train.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【member(会员)】的数据库操作Service实现
* @createDate 2025-11-13 19:52:24
*/
@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member>
    implements MemberService{

    private final MemberMapper memberMapper;

    @Override
    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        Member members = getMembers(mobile);

        if(ObjectUtil.isNull(members)){
            throw new BusinessException(BusinessExceptionEnum.MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowTime());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }

    @Override
    public void sendCode(MemberSendCodeReq mobile) {
        String mobile1 = mobile.getMobile();
        Member members = getMembers(mobile1);
        if(ObjectUtil.isNull(members)){
            log.info("手机号未注册，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowTime());
            member.setMobile(mobile1);
            memberMapper.insert(member);
        }else{
            log.info("手机号已注册");
        }
        //String s = RandomUtil.randomString(4);
        String s="8888";
        log.info("生成验证码：{}", s);

        // 保存短信记录表：手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
         log.info("保存短信记录表");
        // 对接短信通道
        log.info("对接短信通道");
    }

    private Member getMembers(String mobile1) {
        LambdaQueryWrapper<Member> eq = new LambdaQueryWrapper<Member>().eq(Member::getMobile, mobile1);
        List<Member> members = memberMapper.selectList(eq);
        if(CollUtil.isEmpty(members)){
            return null;
        }
        return members.get(0);
    }

    @Override
    public MemberLoginResp login(MemberLoginReq loginReq) {
        String mobile = loginReq.getMobile();
        String code = loginReq.getCode();
        Member members = getMembers(mobile);
        if(ObjectUtil.isNull(members)){
            throw new BusinessException(BusinessExceptionEnum.MOBILE_NOT_EXIST);
        }
        if(!"8888".equals(code)){
            throw new BusinessException(BusinessExceptionEnum.CODE_ERROR);
        }
        MemberLoginResp resp = BeanUtil.copyProperties(members, MemberLoginResp.class);
        String token = JwtUtil.createToken(resp.getId(), resp.getToken());
        resp.setToken(token);
        return resp;
    }
}