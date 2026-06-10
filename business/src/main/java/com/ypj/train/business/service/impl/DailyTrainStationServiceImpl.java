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
import com.ypj.train.business.domain.DailyTrainStation;
import com.ypj.train.business.domain.TrainStation;
import com.ypj.train.business.mapper.DailyTrainStationMapper;
import com.ypj.train.business.req.DailyTrainStationQueryReq;
import com.ypj.train.business.req.DailyTrainStationSaveReq;
import com.ypj.train.business.resp.DailyTrainStationQueryResp;
import com.ypj.train.business.service.DailyTrainStationService;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
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
* @description 针对表【daily_train_station(每日车站)】的数据库操作Service实现
* @createDate 2025-11-26 21:17:40
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainStationServiceImpl extends ServiceImpl<DailyTrainStationMapper, DailyTrainStation>
    implements DailyTrainStationService{

    private final DailyTrainStationMapper dailyTrainStationMapper;

    private final TrainStationServiceImpl trainStationService;

    @Override
    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
        Page<DailyTrainStation> dailyTrainStationPage = new Page<>(req.getPage(), req.getPageSize());

        LambdaQueryWrapper<DailyTrainStation> dailyTrainStationLambdaQueryWrapper = new LambdaQueryWrapper<DailyTrainStation>()
                .orderByDesc(DailyTrainStation::getDate)
                .orderByAsc(DailyTrainStation::getTrainCode)
                .orderByAsc(DailyTrainStation::getIndex);

        if(ObjectUtil.isNotNull(req.getDate())){
            dailyTrainStationLambdaQueryWrapper.eq(DailyTrainStation::getDate, req.getDate());
        }
        if(StrUtil.isNotBlank(req.getTrainCode())){
            dailyTrainStationLambdaQueryWrapper.eq(DailyTrainStation::getTrainCode, req.getTrainCode());
        }

        dailyTrainStationMapper.selectPage(dailyTrainStationPage,dailyTrainStationLambdaQueryWrapper);

        List<DailyTrainStation> records = dailyTrainStationPage.getRecords();
        List<DailyTrainStationQueryResp> dailyTrainStationQueryResps = BeanUtil.copyToList(records, DailyTrainStationQueryResp.class);
        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainStationPage.getTotal());
        pageResp.setRows(dailyTrainStationQueryResps);
        return pageResp;

    }

    @Override
    public void save(DailyTrainStationSaveReq req) {
        DateTime now = DateTime.now();

        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if(ObjectUtil.isNull(dailyTrainStation.getId())){

            DailyTrainStation dailyTrainStation1 = queryBuUnique(dailyTrainStation.getDate()
                    , dailyTrainStation.getTrainCode()
                    , dailyTrainStation.getIndex()
                    , dailyTrainStation.getName());

            if(ObjectUtil.isNotNull(dailyTrainStation1)){
                throw new BusinessException(BusinessExceptionEnum.DAILY_TRAIN_STATION_EXIST);
            }

            dailyTrainStation.setId(SnowUtil.getSnowTime());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);

        }else{
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateById(dailyTrainStation);
        }
    }

    public DailyTrainStation queryBuUnique(Date date, String trainCode,Integer index,String name){
        LambdaQueryWrapper<DailyTrainStation> eq = new LambdaQueryWrapper<DailyTrainStation>()
                .eq(DailyTrainStation::getDate, date)
                .eq(DailyTrainStation::getTrainCode, trainCode)
                .eq(DailyTrainStation::getIndex, index)
                .or()
                .eq(DailyTrainStation::getName, name)
                .eq(DailyTrainStation::getTrainCode, trainCode)
                .eq(DailyTrainStation::getDate, date);

        List<DailyTrainStation> dailyTrainStations = dailyTrainStationMapper.selectList(eq);
        if(CollUtil.isNotEmpty(dailyTrainStations)){
            return dailyTrainStations.get(0);
        }
        return null;
    }

    @Transactional
    public void genDailyStation(Date date,String trainCode) {
        // 删除该日期下编号为trainCode的车站信息
        LambdaQueryWrapper<DailyTrainStation> eq = new LambdaQueryWrapper<DailyTrainStation>()
                .eq(DailyTrainStation::getDate, date)
                .eq(DailyTrainStation::getTrainCode, trainCode);
        dailyTrainStationMapper.delete(eq);

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        if(CollUtil.isEmpty(trainStations)){
            log.info("没有该车次的车站基本数据，生成车次车站结束");
            return;
        }
        for (TrainStation trainStation : trainStations) {
            DateTime now = DateTime.now();

            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);

            dailyTrainStation.setId(SnowUtil.getSnowTime());
            dailyTrainStation.setDate(date);
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);

            log.info("生成日期为【{}】、车次编号为【{}】的【{}】站，站序为【{}】"
                    , DateUtil.formatDate(date)
                    , dailyTrainStation.getTrainCode()
                    , dailyTrainStation.getName()
                    , dailyTrainStation.getIndex()
            );

            dailyTrainStationMapper.insert(dailyTrainStation);
        }

    }

    public List<DailyTrainStation> queryByDataAndCode(Date date,String trainCode) {

        LambdaQueryWrapper<DailyTrainStation> dailyTrainStationLambdaQueryWrapper = new LambdaQueryWrapper<DailyTrainStation>()
                .eq(DailyTrainStation::getDate, date)
                .eq(DailyTrainStation::getTrainCode, trainCode)
                .orderByAsc(DailyTrainStation::getIndex);

        return dailyTrainStationMapper.selectList(dailyTrainStationLambdaQueryWrapper);

    }

    public Integer countStation(Date date, String trainCode) {
        LambdaQueryWrapper<DailyTrainStation> eq = new LambdaQueryWrapper<DailyTrainStation>()
                .eq(DailyTrainStation::getDate, date)
                .eq(DailyTrainStation::getTrainCode, trainCode);
        Long l = dailyTrainStationMapper.selectCount(eq);
        return Math.toIntExact(l);
    }
}




