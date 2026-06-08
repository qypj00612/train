package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.ConfirmOrder;
import com.ypj.train.business.domain.DailyTrainTicket;
import com.ypj.train.business.enums.ConfirmOrderStatusEnum;
import com.ypj.train.business.enums.SeatColEnum;
import com.ypj.train.business.enums.SeatTypeEnum;
import com.ypj.train.business.mapper.ConfirmOrderMapper;
import com.ypj.train.business.req.ConfirmOrderDoReq;
import com.ypj.train.business.req.ConfirmOrderQueryReq;
import com.ypj.train.business.req.ConfirmOrderTicketSaveReq;
import com.ypj.train.business.resp.ConfirmOrderQueryResp;
import com.ypj.train.business.service.ConfirmOrderService;
import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author Ypj
* @description 针对表【confirm_order(确认订单)】的数据库操作Service实现
* @createDate 2025-12-02 21:48:22
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmOrderServiceImpl extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder>
    implements ConfirmOrderService{

    private final ConfirmOrderMapper confirmOrderMapper;

    private final DailyTrainTicketServiceImpl dailyTrainTicketService;

    @Override
    public PageResp<ConfirmOrderQueryResp> query(ConfirmOrderQueryReq req) {
        Page<ConfirmOrder> confirmOrderPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<ConfirmOrder> confirmOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();

        confirmOrderMapper.selectPage(confirmOrderPage,confirmOrderLambdaQueryWrapper);

        List<ConfirmOrder> records = confirmOrderPage.getRecords();
        List<ConfirmOrderQueryResp> confirmOrderQueryResps = BeanUtil.copyToList(records, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> resp = new PageResp<>();
        resp.setTotal(confirmOrderPage.getTotal());
        resp.setRows(confirmOrderQueryResps);

        return resp;
    }

    @Override
    public void doConfirm(ConfirmOrderDoReq req) {
        // 省略业务数据校验, 如: 车次是否存在、余票是否存在、车次是否在有效期内、tickets条数>0、同乘客同一天同车次是否已买过票

        // 保存确认订单表, 状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();

        confirmOrder.setId(SnowUtil.getSnowTime());
        confirmOrder.setMemberId(MemberContext.getId());
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

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService
                .queryByUnique(req.getDate(), req.getTrainCode(), req.getStart(), req.getEnd());
        log.info("查出的余票记录为{}", dailyTrainTicket);
        if(ObjectUtil.isNull(dailyTrainTicket)){
            return;
        }

        // 扣减余票数量，并判断余票是否足够
        reduceTicket(req, dailyTrainTicket);

        // 计算相对第一个座位的偏移值
        List<ConfirmOrderTicketSaveReq> tickets = req.getTickets();
        ConfirmOrderTicketSaveReq ticket0 = tickets.get(0);
        List<Integer> offset = new ArrayList<>();

        if(StrUtil.isNotBlank(ticket0.getSeat())){
            log.info("本次购票有选座");

            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(ticket0.getSeatTypeCode());
            log.info("本次座位的列{}", colsByType);

            List<String> clo = new ArrayList<>();
            for(int i=1;i<=2;i++){
                for (SeatColEnum seatColEnum : colsByType) {
                    clo.add(seatColEnum.getCode()+i);
                }
            }
            log.info("本次座位的参考列{}", clo);

            List<Integer> absoluteOffset = new ArrayList<>();
            for (ConfirmOrderTicketSaveReq ticket : tickets) {
                absoluteOffset.add(clo.indexOf(ticket.getSeat()));
            }
            log.info("本次购票的绝对偏移值{}", absoluteOffset);

            for (Integer i : absoluteOffset) {
                offset.add(i-absoluteOffset.get(0));
            }
            log.info("本次座位的相对偏移值{}", offset);

        }else{
            log.info("本次购票没有选座");
        }

        // 选座

            // 一个车厢一个车厢的获取座位数据

            // 挑选符合条件的座位，如果这个车厢不满足，则进入下一个车厢（多个选座应该在同一个车厢）


        // 挑选座位后事务处理

            // 座位表修改售卖情况sell

            // 余票详情表修改余票数量

            // 为会员添加购票记录

            // 更新确认订单表为成功
    }

    private static void reduceTicket(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for(ConfirmOrderTicketSaveReq confirm: req.getTickets()){
            SeatTypeEnum by = EnumUtil.getBy(SeatTypeEnum::getCode, confirm.getSeatTypeCode());
            switch(by){
                case YDZ -> {
                    int count = dailyTrainTicket.getYdz()-1;
                    if(count<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFiRM_ORDER_TICKET_ERROR);
                    }
                    dailyTrainTicket.setYdz(count);
                }
                case EDZ -> {
                    int count = dailyTrainTicket.getEdz()-1;
                    if(count<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFiRM_ORDER_TICKET_ERROR);
                    }
                    dailyTrainTicket.setEdz(count);
                }
                case YW -> {
                    int count = dailyTrainTicket.getYw()-1;
                    if(count<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFiRM_ORDER_TICKET_ERROR);
                    }
                    dailyTrainTicket.setYw(count);
                }
                case RW -> {
                    int count = dailyTrainTicket.getRw()-1;
                    if(count<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFiRM_ORDER_TICKET_ERROR);
                    }
                    dailyTrainTicket.setRw(count);
                }
            }
        }
    }
}




