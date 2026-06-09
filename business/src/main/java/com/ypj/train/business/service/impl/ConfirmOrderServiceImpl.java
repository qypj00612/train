package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.api.client.MemberClient;
import com.ypj.train.business.domain.ConfirmOrder;
import com.ypj.train.business.domain.DailyTrainCarriage;
import com.ypj.train.business.domain.DailyTrainSeat;
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
import com.ypj.train.common.domain.TicketSaveReq;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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

    private final DailyTrainCarriageServiceImpl dailyTrainCarriageService;

    private final DailyTrainSeatServiceImpl dailyTrainSeatService;

    private final MemberClient memberClient;

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

        List<DailyTrainSeat> finDailyTrainSeats = new ArrayList<>();

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

            List<Integer> offset = new ArrayList<>();
            for (Integer i : absoluteOffset) {
                offset.add(i-absoluteOffset.get(0));
            }
            log.info("本次座位的相对偏移值{}", offset);

            getSeat(finDailyTrainSeats
                    , req.getDate()
                    , req.getTrainCode()
                    , ticket0.getSeatTypeCode()
                    , ticket0.getSeat().split("")[0]
                    , offset
                    , dailyTrainTicket.getStartIndex()
                    , dailyTrainTicket.getEndIndex());
        }else{
            log.info("本次购票没有选座");

            for (ConfirmOrderTicketSaveReq ticket : tickets) {
                getSeat(finDailyTrainSeats
                        , req.getDate()
                        , req.getTrainCode()
                        , ticket.getSeatTypeCode()
                        , null
                        , null
                        , dailyTrainTicket.getStartIndex()
                        , dailyTrainTicket.getEndIndex());
            }

        }
        log.info("最终选择的座位{}", finDailyTrainSeats);

        // 代理对象
        ConfirmOrderServiceImpl proxy = (ConfirmOrderServiceImpl)AopContext.currentProxy();
        proxy.afterConfirm(dailyTrainTicket, finDailyTrainSeats, req.getTickets(), confirmOrder);


            // 座位表修改售卖情况sell

            // 余票详情表修改余票数量

            // 为会员添加购票记录

            // 更新确认订单表为成功
    }

    /**
     * 挑选座位后事务处理
     * @param dailyTrainTicket 余票信息
     * @param finDailyTrainSeats 最终选择的座位信息
     * @param tickets 购买的车票信息
     * @param confirmOrder 订单信息
     */
    @Transactional
    public void afterConfirm(DailyTrainTicket dailyTrainTicket
            , List<DailyTrainSeat> finDailyTrainSeats
            , List<ConfirmOrderTicketSaveReq> tickets
            , ConfirmOrder confirmOrder){
        for (int j = 0; j < finDailyTrainSeats.size(); j++) {
            DailyTrainSeat finDailyTrainSeat = finDailyTrainSeats.get(j);
            // 座位表修改售卖情况sell
            dailyTrainSeatService.updateSell(finDailyTrainSeat);

            // 余票详情表修改余票数量
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            Integer maxStartIndex = endIndex - 1;
            Integer minStartIndex = 0;
            Integer minEndIndex = startIndex + 1;
            Integer maxEndIndex = finDailyTrainSeat.getSell().length();

            // 0000100001
            //       111
            // 1000000000
            //   1
            char[] sellArray = finDailyTrainSeat.getSell().toCharArray();
            for(int i=startIndex-1; i>=0; i--){
                if(sellArray[i]=='1'){
                    minStartIndex=i+1;
                    break;
                }
            }
            log.info("影响的出发站区间为{}~{}", minStartIndex, maxStartIndex);

            for(int i=endIndex;i<sellArray.length;i++){
                if(sellArray[i]=='1'){
                    maxEndIndex=i;
                    break;
                }
            }
            log.info("影响的终点站区间为{}~{}", minEndIndex, maxEndIndex);

            log.info("影响的站位区间为{}~{}", minStartIndex, maxEndIndex);

            dailyTrainTicketService.updateTicketCount(
                    finDailyTrainSeat.getDate()
                    , finDailyTrainSeat.getTrainCode()
                    , finDailyTrainSeat.getSeatType()
                    , maxStartIndex
                    , minStartIndex, minEndIndex, maxEndIndex);

            // 为会员添加购票记录
            TicketSaveReq ticketSaveReq = new TicketSaveReq();

            ticketSaveReq.setId(SnowUtil.getSnowTime());
            ticketSaveReq.setMemberId(MemberContext.getId());
            ticketSaveReq.setPassengerId(tickets.get(j).getPassengerId());
            ticketSaveReq.setPassengerName(tickets.get(j).getPassengerName());
            ticketSaveReq.setDate(finDailyTrainSeat.getDate());
            ticketSaveReq.setTrainCode(finDailyTrainSeat.getTrainCode());
            ticketSaveReq.setCarriageIndex(finDailyTrainSeat.getCarriageIndex());
            ticketSaveReq.setRow(finDailyTrainSeat.getRow());
            ticketSaveReq.setCol(finDailyTrainSeat.getCol());
            ticketSaveReq.setStart(dailyTrainTicket.getStart());
            ticketSaveReq.setStartTime(dailyTrainTicket.getStartTime());
            ticketSaveReq.setEnd(dailyTrainTicket.getEnd()) ;
            ticketSaveReq.setEndTime(dailyTrainTicket.getEndTime());
            ticketSaveReq.setSeatType(finDailyTrainSeat.getSeatType());

            memberClient.save(ticketSaveReq);
        }

        // 更新确认订单表为成功
        ConfirmOrder updateConfirmOrder = new ConfirmOrder();
        updateConfirmOrder.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
        DateTime now = DateTime.now();
        updateConfirmOrder.setUpdateTime(now);
        LambdaUpdateWrapper<ConfirmOrder> eq = new LambdaUpdateWrapper<ConfirmOrder>()
                .eq(ConfirmOrder::getId, confirmOrder.getId());

        confirmOrderMapper.update(updateConfirmOrder, eq);
    }

    /**
     * 选座
     * @param finDailyTrainSeats 最终选择的座位
     * @param date 日期
     * @param trainCode 车次
     * @param seatType 座位类型
     * @param col 列号 | null表示未选座
     * @param offset 偏移量 | null表示未选座
     * @param startIndex 起始站序号
     * @param endIndex 到达站序号
     */
    public void getSeat(List<DailyTrainSeat> finDailyTrainSeats
            , Date date, String trainCode, String seatType
            , String col, List<Integer> offset
            , Integer startIndex, Integer endIndex){
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageService
                .selectBySeatType(date, trainCode, seatType);
        log.info("共有{}个符合条件的车厢", dailyTrainCarriages.size());

        // 一个车厢一个车厢的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriages) {
            List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatService
                    .selectByCarriageIndex(date, trainCode, dailyTrainCarriage.getIndex());
            log.info("{}号车厢共有座位数:{}", dailyTrainCarriage.getIndex(), dailyTrainSeats.size());

            for (DailyTrainSeat dailyTrainSeat : dailyTrainSeats) {

                // 存储临时选中的座位
                List<DailyTrainSeat> getDailyTrainSeat = new ArrayList<>();

                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();

                if(StrUtil.isNotBlank(col)&&!col.equals(dailyTrainSeat.getCol())){
                    log.info("座位{}的列号为{}, 与目标列号{}不符", seatIndex
                            , dailyTrainSeat.getCol(), col);
                    continue;
                }

                // 判断最终选择的座位中包不包含临时选择的座位
                boolean isHChoose = false;
                if(StrUtil.isBlank(col)){
                    // 使用id判断, 不能使用对象判断, 因为选中的座位的sell信息改变了
                    for (DailyTrainSeat finDailyTrainSeat : finDailyTrainSeats) {
                        if(finDailyTrainSeat.getId().equals(dailyTrainSeat.getId())){
                            isHChoose = true;
                            break;
                        }
                    }
                }
                if(isHChoose){
                    log.info("座位{}被选中过, 不能重复选中", seatIndex);
                    continue;
                }

                // 若该座位不能被选中则结束本次循环, 继续进行下一次循环判断
                if(calSellIsNot(dailyTrainSeat, startIndex, endIndex)){
                    continue;
                }
                log.info("选择座位{}", seatIndex);
                getDailyTrainSeat.add(dailyTrainSeat);

                // 判断是否能一次同时选完
                boolean notAll = false;
                if(CollUtil.isNotEmpty(offset)){
                    log.info("有偏移值{}, 校验偏移的座位是否可选", offset);
                    for(int i=1;i<offset.size();i++){
                        int nextIndex = seatIndex+offset.get(i)-1;

                        if(nextIndex>=dailyTrainSeats.size()){
                            // 选择的座位不在同意车厢
                            log.info("座位序号{}超出车厢座位序号数", nextIndex+1);
                            notAll = true;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = dailyTrainSeats.get(nextIndex);
                        if(calSellIsNot(nextDailyTrainSeat, startIndex, endIndex)){
                            // 偏移的座位不能选中
                            notAll = true;
                            break;
                        }
                        log.info("选择座位{}", nextDailyTrainSeat.getCarriageSeatIndex());
                        getDailyTrainSeat.add(nextDailyTrainSeat);
                    }
                }
                if(notAll){
                    log.info("重新选择座位");
                    continue;
                }

                finDailyTrainSeats.addAll(getDailyTrainSeat);
                return;

            }

        }

    }

    /**
     * 判断该座位是不是不可以出售
     * @param dailyTrainSeat 判断的座位
     * @param startIndex 起始站序号
     * @param endIndex 到达站序号
     * @return 是否不可售
     */
    private boolean calSellIsNot(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex){
        // 00000
        String sell = dailyTrainSeat.getSell();
        //  000
        String curSell = sell.substring(startIndex, endIndex);
        if(Integer.parseInt(curSell)!=0){
            log.info("座位{}已售卖过票, 区间{}~{}不可选", dailyTrainSeat.getCarriageSeatIndex()
                , startIndex, endIndex);
            return true;
        }
        //  111
        curSell = curSell.replace('0','1');
        // 0111
        curSell = StrUtil.fillBefore(curSell, '0', endIndex);
        // 01110
        curSell = StrUtil.fillAfter(curSell, '0', sell.length());
        // 将当前的售卖信息与原售卖信息做位或运算即可得到最终的售卖信息
        // 15(01110)
        long finSellInt = NumberUtil.binaryToLong(sell)|NumberUtil.binaryToLong(curSell);
        //  1110
        String finSell = NumberUtil.getBinaryStr(finSellInt);
        // 01110
        // 因为转换成字符串形式时可能会丢失前缀0, 故应补上前面的0
        finSell = StrUtil.fillBefore(finSell, '0', sell.length());
        log.info("座位{}可售, 区间{}~{}, 原售卖信息{}, 售卖区间信息{}, 最终售卖信息{}"
                ,dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex, sell, curSell, finSell);

        dailyTrainSeat.setSell(finSell);
        return false;
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




