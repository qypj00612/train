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
import com.ypj.train.business.domain.DailyTrain;
import com.ypj.train.business.domain.Train;
import com.ypj.train.business.mapper.DailyTrainMapper;
import com.ypj.train.business.req.DailyTrainQueryReq;
import com.ypj.train.business.req.DailyTrainSaveReq;
import com.ypj.train.business.resp.DailyTrainQueryResp;
import com.ypj.train.business.service.DailyTrainService;
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
* @description 针对表【daily_train(每日车次)】的数据库操作Service实现
* @createDate 2025-11-26 20:01:18
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainServiceImpl extends ServiceImpl<DailyTrainMapper, DailyTrain>
    implements DailyTrainService{

    private final DailyTrainMapper dailyTrainMapper;

    private final TrainServiceImpl trainService;

    private final DailyTrainStationServiceImpl dailyTrainStationService;

    private final DailyTrainCarriageServiceImpl dailyTrainCarriageService;

    private final DailyTrainSeatServiceImpl dailyTrainSeatService;

    private final DailyTrainTicketServiceImpl dailyTrainTicketService;

    @Override
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        Page<DailyTrain> dailyTrainPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<DailyTrain> dailyTrainLambdaQueryWrapper = new LambdaQueryWrapper<DailyTrain>()
                .orderByDesc(DailyTrain::getDate)
                .orderByAsc(DailyTrain::getCode);

        if(ObjectUtil.isNotNull(req.getDate())){
            dailyTrainLambdaQueryWrapper.eq(DailyTrain::getDate, req.getDate());
        }
        if(StrUtil.isNotBlank(req.getCode())){
            dailyTrainLambdaQueryWrapper.eq(DailyTrain::getCode, req.getCode());
        }

        dailyTrainMapper.selectPage(dailyTrainPage,dailyTrainLambdaQueryWrapper);

        List<DailyTrain> dailyTrains = dailyTrainPage.getRecords();
        List<DailyTrainQueryResp> dailyTrainQueryResps = BeanUtil.copyToList(dailyTrains, DailyTrainQueryResp.class);
        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainPage.getTotal());
        pageResp.setRows(dailyTrainQueryResps);
        return pageResp;
    }

    @Override
    public void save(DailyTrainSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);

        if(ObjectUtil.isNull(dailyTrain.getId())){

            DailyTrain dailyTrain1 = selectByUnique(req.getCode(), req.getDate());
            if(ObjectUtil.isNotNull(dailyTrain1)){
                throw new BusinessException(BusinessExceptionEnum.DAILY_TRAIN_EXIST);
            }

            System.out.println(dailyTrain);

            dailyTrain.setId(SnowUtil.getSnowTime());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        }else{
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateById(dailyTrain);
        }
    }

    public DailyTrain selectByUnique(String code, Date date){
        LambdaQueryWrapper<DailyTrain> eq = new LambdaQueryWrapper<DailyTrain>()
                .eq(DailyTrain::getCode, code)
                .eq(DailyTrain::getDate, date);
        List<DailyTrain> dailyTrains = dailyTrainMapper.selectList(eq);
        if(CollUtil.isNotEmpty(dailyTrains)){
            return dailyTrains.get(0);
        }
        return null;
    }

    /**
     * 生成该日所有的车次信息，(车次、车站、车厢、座位)
     * @param data 日期
     */
    @Override
    @Transactional
    public void genDaily(Date data) {
        List<Train> trains = trainService.selectAll();
        if(CollUtil.isEmpty(trains)){
            log.info("没有基础车次数据，生成结束");
            return;
        }
        for (Train train : trains) {
            genDailyTrain(data,train);
        }
    }

    @Transactional
    public void genDailyTrain(Date date, Train train) {
        // 删除每日的车次数据
        LambdaQueryWrapper<DailyTrain> eq = new LambdaQueryWrapper<DailyTrain>()
                .eq(DailyTrain::getDate, date)
                .eq(DailyTrain::getCode, train.getCode());
        dailyTrainMapper.delete(eq);

        // 生成每日的车次数据
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);

        dailyTrain.setDate(date);
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setId(SnowUtil.getSnowTime());

        log.info("生成日期为【{}】、编号为【{}】的车次", DateUtil.formatDate(date),train.getCode());
        dailyTrainMapper.insert(dailyTrain);

        // 生成每日车次车站
        dailyTrainStationService.genDailyStation(date,train.getCode());

        // 生成每日车次车厢
        dailyTrainCarriageService.genDailyStation(date,train.getCode());

        // 生成每日车次座位
        dailyTrainSeatService.genDailyStation(date,train.getCode());

        // 生成每日余票信息
        dailyTrainTicketService.genDailyTicket(dailyTrain,date,train.getCode());
    }
}




