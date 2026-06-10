package com.ypj.train.business.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ypj.train.business.req.ConfirmOrderDoReq;
import com.ypj.train.business.service.BeforeConfirmOrder;
import com.ypj.train.business.service.ConfirmOrderService;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("business/confirm-order")
@Slf4j
public class ConfirmOrderController {

    private final ConfirmOrderService confirmOrderService;

    private final StringRedisTemplate redisTemplate;

    private final BeforeConfirmOrder beforeConfirmOrder;

    @Value("spring.profiles.active")
    private String env;

    @PostMapping("do")
    @SentinelResource(value = "confirm-order", blockHandler = "doConfirmBlock")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        if (env.equals("dev")) {
            String imageCodeToken = req.getImageCodeToken();
            String imageCode = req.getImageCode();
            String s = redisTemplate.opsForValue().get(imageCodeToken);
            if(ObjectUtil.isEmpty(s)){
                return new CommonResp<>(false, "验证码已过期", null);
            }
            if(!s.equalsIgnoreCase(imageCode)){
                return new CommonResp<>(false, "验证码不正确", null);
            }else{
                redisTemplate.delete(imageCodeToken);
            }
        }

        Long id = beforeConfirmOrder.beforeDoConfirm(req);
        //confirmOrderService.doConfirm(req);
        return new CommonResp<>(String.valueOf(id));
    }

    @GetMapping("query-line-count/{id}")
    public CommonResp<Integer> queryLineCount(@PathVariable Long id) {
        Integer count = confirmOrderService.queryLineCount(id);
        return new CommonResp<>(count);
    }

    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException blockException) {
        log.info("购票请求被限流: {}", req);
        throw new BusinessException(BusinessExceptionEnum.TICKET_EXCEPTION_FLOW);
    }

    @GetMapping("hello")
    public CommonResp<String> hello() {
        return new CommonResp<>("hello");
    }
}
