package com.ypj.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.resp.MemberLoginResp;
import com.ypj.train.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        log.info("拦截开始");
        if(StrUtil.isNotBlank(token)){
            log.info("获取登录会员token: {}", token);
            JSONObject jsonObject = JwtUtil.getJSONObject(token);
            log.info("原始会员数据为: {}", jsonObject);
            MemberLoginResp bean = JSONUtil.toBean(jsonObject, MemberLoginResp.class);
            log.info("当前会员为: {}", bean);
            MemberContext.setMember(bean);
        }
        return true;
    }
}
