package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.DailyTrainSeat;
import com.ypj.train.business.domain.TrainSeat;
import com.ypj.train.business.domain.TrainStation;
import com.ypj.train.business.mapper.DailyTrainSeatMapper;
import com.ypj.train.business.req.DailyTrainSeatQueryReq;
import com.ypj.train.business.resp.DailyTrainSeatQueryResp;
import com.ypj.train.business.service.DailyTrainSeatService;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author Ypj
* @description 针对表【daily_train_seat(每日座位)】的数据库操作Service实现
* @createDate 2025-11-27 19:37:17
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainSeatServiceImpl extends ServiceImpl<DailyTrainSeatMapper, DailyTrainSeat>
    implements DailyTrainSeatService{

    private final DailyTrainSeatMapper dailyTrainSeatMapper;

    private final TrainSeatServiceImpl trainSeatService;

    private final TrainStationServiceImpl trainStationService;

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        Page<DailyTrainSeat> dailyTrainSeatPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<DailyTrainSeat> dailyTrainSeatLambdaQueryWrapper = new LambdaQueryWrapper<DailyTrainSeat>()
                .orderByDesc(DailyTrainSeat::getDate)
                .orderByAsc(DailyTrainSeat::getTrainCode)
                .orderByAsc(DailyTrainSeat::getCarriageIndex)
                .orderByAsc(DailyTrainSeat::getCarriageSeatIndex);

        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            dailyTrainSeatLambdaQueryWrapper.eq(DailyTrainSeat::getTrainCode, req.getTrainCode());
        }

        dailyTrainSeatMapper.selectPage(dailyTrainSeatPage,dailyTrainSeatLambdaQueryWrapper);

        List<DailyTrainSeat> records = dailyTrainSeatPage.getRecords();
        List<DailyTrainSeatQueryResp> dailyTrainSeatQueryResps = BeanUtil.copyToList(records, DailyTrainSeatQueryResp.class);
        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainSeatPage.getTotal());
        pageResp.setRows(dailyTrainSeatQueryResps);
        return pageResp;
    }

    @Transactional
    public void genDailyStation(Date date, String code) {
        // 删除data日，编号为code的车次座位信息
        LambdaQueryWrapper<DailyTrainSeat> eq = new LambdaQueryWrapper<DailyTrainSeat>()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, code);
        dailyTrainSeatMapper.delete(eq);

        List<TrainSeat> trainSeats = trainSeatService.selectByTrainCode(code);
        if(CollUtil.isEmpty(trainSeats)){
            log.info("没有该车次的基础座位信息，生成结束");
            return;
        }

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(code);
        String sell = StrUtil.fillBefore("", '0',trainStations.size()-1);

        for (TrainSeat trainSeat : trainSeats) {
            DateTime now = DateTime.now();

            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);

            dailyTrainSeat.setId(SnowUtil.getSnowTime());
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);

            log.info("生成车次为【{}】，日期为【{}】、【{}】车厢的【{}】号座"
                    ,code, DateUtil.formatDate(date)
                    ,dailyTrainSeat.getCarriageIndex(),dailyTrainSeat.getCarriageSeatIndex());

            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
    }

    public Integer countSeat(Date date, String seatCode, String seatType) {
        LambdaQueryWrapper<DailyTrainSeat> eq = new LambdaQueryWrapper<DailyTrainSeat>()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, seatCode)
                .eq(DailyTrainSeat::getSeatType, seatType);
        Long l = dailyTrainSeatMapper.selectCount(eq);
        return Math.toIntExact(l.equals(0L) ? -1 : l);
    }

    public List<DailyTrainSeat> selectByCarriageIndex(Date date, String trainCode, Integer carriageIndex) {
        LambdaQueryWrapper<DailyTrainSeat> eq = new LambdaQueryWrapper<DailyTrainSeat>()
                .eq(DailyTrainSeat::getDate, DateUtil.beginOfDay(date))
                .eq(DailyTrainSeat::getTrainCode, trainCode)
                .eq(DailyTrainSeat::getCarriageIndex, carriageIndex)
                .orderByAsc(DailyTrainSeat::getCarriageSeatIndex);

        return dailyTrainSeatMapper.selectList(eq);
    }

    public void updateSell(DailyTrainSeat finDailyTrainSeat) {

            DailyTrainSeat dailyTrainSeatDB = new DailyTrainSeat();
            dailyTrainSeatDB.setId(finDailyTrainSeat.getId());
            dailyTrainSeatDB.setSell(finDailyTrainSeat.getSell());

            LambdaQueryWrapper<DailyTrainSeat> eq = new LambdaQueryWrapper<DailyTrainSeat>()
                    .eq(DailyTrainSeat::getId, dailyTrainSeatDB.getId());

            dailyTrainSeatMapper.update(dailyTrainSeatDB,eq);

    }
}




