package com.ypj.train.business.mq;

import cn.hutool.json.JSONUtil;
import com.ypj.train.business.domain.ConfirmOrderMQDTO;
import com.ypj.train.business.service.ConfirmOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
@RequiredArgsConstructor
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

    private final ConfirmOrderService confirmOrderService;

    @Override
    public void onMessage(MessageExt message) {
        byte[] body = message.getBody();
        ConfirmOrderMQDTO bean = JSONUtil.toBean(new String(body), ConfirmOrderMQDTO.class);
        MDC.put("LOG_ID",bean.getLogId());
        log.info("接收到消息:{}", new String(body));
        confirmOrderService.doConfirm(bean);
    }
}
