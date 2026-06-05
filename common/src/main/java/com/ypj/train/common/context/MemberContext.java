package com.ypj.train.common.context;

import com.ypj.train.common.resp.MemberLoginResp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberContext {
    private final static ThreadLocal<MemberLoginResp> threadLocal = new ThreadLocal<>();

    public static MemberLoginResp getMember(){
        return threadLocal.get();
    }

    public static void setMember(MemberLoginResp member){
        threadLocal.set(member);
    }

    public static Long getId(){
        try {
            return threadLocal.get().getId();
        } catch (Exception e) {
            log.error("获取会员id错误：{}", e.getMessage());
            throw e;
        }
    }
}
