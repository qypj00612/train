package com.ypj.train.business.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ypj.train.business.domain.ConfirmOrder;
import com.ypj.train.business.domain.ConfirmOrderMQDTO;
import com.ypj.train.business.enums.ConfirmOrderStatusEnum;
import com.ypj.train.business.enums.RocketMQTopicEnum;
import com.ypj.train.business.mapper.ConfirmOrderMapper;
import com.ypj.train.business.req.ConfirmOrderDoReq;
import com.ypj.train.business.service.BeforeConfirmOrder;
import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.CommonResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BeforeConfirmOrderImpl implements BeforeConfirmOrder {

    private final RedissonClient redissonClient;

    private final SkTokenServiceImpl skTokenService;

    private final ConfirmOrderMapper confirmOrderMapper;

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req) {
        req.setMemberId(MemberContext.getId());

        boolean validSkToken = skTokenService.valid(req.getDate(),req.getTrainCode(),req.getMemberId());
        if(Boolean.FALSE.equals(validSkToken)){
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_FALSE);
        }

        // 保存确认订单表, 状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();

        confirmOrder.setId(SnowUtil.getSnowTime());
        confirmOrder.setMemberId(req.getMemberId());
        confirmOrder.setDate(req.getDate());
        confirmOrder.setTrainCode(req.getTrainCode());
        confirmOrder.setStart(req.getStart());
        confirmOrder.setEnd(req.getEnd());
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setTickets(JSONUtil.toJsonStr(req.getTickets()));
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);

        log.info("新建订单为{}", confirmOrder);
        confirmOrderMapper.insert(confirmOrder);

        ConfirmOrderMQDTO confirmOrderMQDTO = new ConfirmOrderMQDTO();
        confirmOrderMQDTO.setLogId(MDC.get("LOG_ID"));
        confirmOrderMQDTO.setDate(confirmOrder.getDate());
        confirmOrderMQDTO.setTrainCode(confirmOrder.getTrainCode());

        // MQ
        String jsonStr = JSONUtil.toJsonStr(confirmOrderMQDTO);
        log.info("向MQ发送信息: {}",jsonStr);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(),jsonStr);
        log.info("信息发送完毕");

//        String key = RedisPreEnum.ORDER_LOCK.getDesc()+req.getDate()+"-"+req.getTrainCode();
//        RLock lock = null;
//        try {
//            lock = redissonClient.getLock(key);
//
//            //lock.tryLock(10, 30, TimeUnit.SECONDS); 不带看门狗
//            /**
//             * waitTime 等待获取锁时间, 超时返回false
//             * leaseTime 锁时长, 即n秒后自动释放锁
//             * time unit 时间单位
//             */
//            boolean b = lock.tryLock(0, TimeUnit.SECONDS); // 带看门狗
//
//            if(Boolean.FALSE.equals(b)) {
//                log.info("没有抢到锁");
//                throw new BusinessException(BusinessExceptionEnum.TICKET_EXCEPTION_LOCK);
//            }
//            log.info("抢到锁了");
//
//            // MQ
//            String jsonStr = JSONUtil.toJsonStr(req);
//            log.info("向MQ发送信息: {}",jsonStr);
//            rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(),jsonStr);
//            log.info("信息发送完毕");
//
//        } catch (Exception e) {
//            log.info("购票异常");
//            throw new BusinessException(BusinessExceptionEnum.TICKET_EXCEPTION_LOCK);
//        } finally {
//            if(lock != null && lock.isHeldByCurrentThread()){
//                lock.unlock();
//            }
//        }
    }

    public CommonResp<Object> beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException blockException) {
        log.info("限流");
        throw new BusinessException(BusinessExceptionEnum.TICKET_EXCEPTION_FLOW);
    }
}
