package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.DailyTrain;
import com.ypj.train.business.domain.DailyTrainStation;
import com.ypj.train.business.domain.DailyTrainTicket;
import com.ypj.train.business.enums.SeatTypeEnum;
import com.ypj.train.business.enums.TrainTypeEnum;
import com.ypj.train.business.mapper.DailyTrainTicketMapper;
import com.ypj.train.business.req.DailyTrainTicketQueryReq;
import com.ypj.train.business.resp.DailyTrainTicketQueryResp;
import com.ypj.train.business.service.DailyTrainTicketService;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
* @author Ypj
* @description 针对表【daily_train_ticket(余票信息)】的数据库操作Service实现
* @createDate 2025-12-01 18:26:49
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainTicketServiceImpl extends ServiceImpl<DailyTrainTicketMapper, DailyTrainTicket>
    implements DailyTrainTicketService{

    private final DailyTrainTicketMapper dailyTrainTicketMapper;

    private final DailyTrainStationServiceImpl dailyTrainStationService;

    private final DailyTrainSeatServiceImpl dailyTrainSeatService;

    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        Page<DailyTrainTicket> dailyTrainTicketPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<DailyTrainTicket> dailyTrainTicketLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StrUtil.isNotBlank(req.getTrainCode())){
            dailyTrainTicketLambdaQueryWrapper.eq(DailyTrainTicket::getTrainCode, req.getTrainCode());
        }
        if(ObjectUtil.isNotNull(req.getDate())){
            dailyTrainTicketLambdaQueryWrapper.eq(DailyTrainTicket::getDate, req.getDate());
        }
        if(StrUtil.isNotBlank(req.getStart())){
            dailyTrainTicketLambdaQueryWrapper.eq(DailyTrainTicket::getStart, req.getStart());
        }
        if(StrUtil.isNotBlank(req.getEnd())){
            dailyTrainTicketLambdaQueryWrapper.eq(DailyTrainTicket::getEnd, req.getEnd());
        }

        dailyTrainTicketMapper.selectPage(dailyTrainTicketPage, dailyTrainTicketLambdaQueryWrapper);

        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketPage.getRecords();
        List<DailyTrainTicketQueryResp> dailyTrainTicketQueryResps = BeanUtil.copyToList(dailyTrainTickets, DailyTrainTicketQueryResp.class);

        PageResp<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespPageResp = new PageResp<>();
        dailyTrainTicketQueryRespPageResp.setTotal(dailyTrainTicketPage.getTotal());
        dailyTrainTicketQueryRespPageResp.setRows(dailyTrainTicketQueryResps);
        return dailyTrainTicketQueryRespPageResp;
    }

    @Transactional
    public void genDailyTicket(DailyTrain train, Date date, String code) {
        // 删除该日期及车次编号的余票信息
        LambdaQueryWrapper<DailyTrainTicket> eq = new LambdaQueryWrapper<DailyTrainTicket>()
                .eq(DailyTrainTicket::getDate, date)
                .eq(DailyTrainTicket::getTrainCode, code);
        dailyTrainTicketMapper.delete(eq);

        // 查询该从车次途径的所以车站信息
        List<DailyTrainStation> dailyTrainStations = dailyTrainStationService.queryByDataAndCode(date, code);
        if(CollUtil.isEmpty(dailyTrainStations)){
            log.info("没有该日的途径车站信息，生成余票信息结束");
            return;
        }
        log.info("生成【{}】车次编号为【{}】的余票信息", DateUtil.formatDate(date),code);

        // 获取车次类型
        BigDecimal fieldBy = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, train.getType());

        for(int i = 0; i < dailyTrainStations.size(); i++){
            DailyTrainStation dailyTrainStationStart = dailyTrainStations.get(i);
            BigDecimal km = BigDecimal.ZERO;
            for(int j= i + 1; j < dailyTrainStations.size(); j++){
                DateTime now = DateTime.now();
                DailyTrainStation dailyTrainStationEnd = dailyTrainStations.get(j);

                km = km.add(dailyTrainStationEnd.getKm());

                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowTime());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(code);

                dailyTrainTicket.setStart(dailyTrainStationStart.getName());
                dailyTrainTicket.setStartPinyin(dailyTrainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(dailyTrainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(dailyTrainStationStart.getIndex());

                dailyTrainTicket.setEnd(dailyTrainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(dailyTrainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(dailyTrainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(dailyTrainStationEnd.getIndex());

                Integer ydzCount = dailyTrainSeatService.countSeat(date, code, SeatTypeEnum.YDZ.getCode());
                dailyTrainTicket.setYdz(ydzCount);// 一等座数量

                BigDecimal ydzPrice = km.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(fieldBy).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdzPrice(ydzPrice);// 一等座价格

                Integer edzCount = dailyTrainSeatService.countSeat(date, code, SeatTypeEnum.EDZ.getCode());
                dailyTrainTicket.setEdz(edzCount);// 二等座数量
                BigDecimal edzPrice = km.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(fieldBy).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setEdzPrice(edzPrice);// 二等座价格

                Integer rwCount = dailyTrainSeatService.countSeat(date, code, SeatTypeEnum.RW.getCode());
                dailyTrainTicket.setRw(rwCount);// 软卧数量
                BigDecimal rwPrice = km.multiply(SeatTypeEnum.RW.getPrice()).multiply(fieldBy).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setRwPrice(rwPrice);// 软卧价格

                Integer ywCount = dailyTrainSeatService.countSeat(date, code, SeatTypeEnum.YW.getCode());
                dailyTrainTicket.setYw(ywCount);// 硬卧数量
                BigDecimal ywPrice = km.multiply(SeatTypeEnum.YW.getPrice()).multiply(fieldBy).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYwPrice(ywPrice);// 硬卧价格

                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);

                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
    }

    public DailyTrainTicket queryByUnique(Date date, String code, String start, String end) {
        LambdaQueryWrapper<DailyTrainTicket> eq = new LambdaQueryWrapper<DailyTrainTicket>()
                .eq(DailyTrainTicket::getDate, DateUtil.beginOfDay(date))
                .eq(DailyTrainTicket::getTrainCode, code)
                .eq(DailyTrainTicket::getStart, start)
                .eq(DailyTrainTicket::getEnd, end);

        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectList(eq);
        if(CollUtil.isNotEmpty(dailyTrainTickets)){
            return dailyTrainTickets.get(0);
        }
        return null;
    }
}




