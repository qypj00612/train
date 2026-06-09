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
import com.ypj.train.business.domain.DailyTrainCarriage;
import com.ypj.train.business.domain.TrainCarriage;
import com.ypj.train.business.enums.SeatColEnum;
import com.ypj.train.business.mapper.DailyTrainCarriageMapper;
import com.ypj.train.business.req.DailyTrainCarriageQueryReq;
import com.ypj.train.business.req.DailyTrainCarriageSaveReq;
import com.ypj.train.business.resp.DailyTrainCarriageResp;
import com.ypj.train.business.service.DailyTrainCarriageService;
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
* @description 针对表【daily_train_carriage(每日车厢)】的数据库操作Service实现
* @createDate 2025-11-26 21:57:27
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainCarriageServiceImpl extends ServiceImpl<DailyTrainCarriageMapper, DailyTrainCarriage>
    implements DailyTrainCarriageService{

    private final DailyTrainCarriageMapper dailyTrainCarriageMapper;

    private final TrainCarriageServiceImpl trainCarriageService;

    @Override
    public PageResp<DailyTrainCarriageResp> queryList(DailyTrainCarriageQueryReq req) {
        Page<DailyTrainCarriage> dailyTrainCarriagePage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<DailyTrainCarriage> dailyTrainCarriageLambdaQueryWrapper = new LambdaQueryWrapper<DailyTrainCarriage>()
                .orderByDesc(DailyTrainCarriage::getDate)
                .orderByAsc(DailyTrainCarriage::getTrainCode)
                .orderByAsc(DailyTrainCarriage::getIndex);

        if(ObjectUtil.isNotNull(req.getDate())){
            dailyTrainCarriageLambdaQueryWrapper.eq(DailyTrainCarriage::getDate, req.getDate());
        }
        if(StrUtil.isNotBlank(req.getTrainCode())){
            dailyTrainCarriageLambdaQueryWrapper.eq(DailyTrainCarriage::getTrainCode, req.getTrainCode());
        }

        dailyTrainCarriageMapper.selectPage(dailyTrainCarriagePage,dailyTrainCarriageLambdaQueryWrapper);
        List<DailyTrainCarriage> records = dailyTrainCarriagePage.getRecords();
        List<DailyTrainCarriageResp> dailyTrainCarriageResps = BeanUtil.copyToList(records, DailyTrainCarriageResp.class);

        PageResp<DailyTrainCarriageResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainCarriagePage.getTotal());
        pageResp.setRows(dailyTrainCarriageResps);

        return pageResp;

    }

    @Override
    public void save(DailyTrainCarriageSaveReq req) {
        DateTime now = DateTime.now();

        List<SeatColEnum> colsByType = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(colsByType.size());
        req.setSeatCount(req.getRowCount()*colsByType.size());

        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);

        if(ObjectUtil.isNull(req.getId())){

            DailyTrainCarriage dailyTrainCarriage1 = queryBuUnique(req.getDate(), req.getTrainCode(), req.getIndex());
            if(ObjectUtil.isNotNull(dailyTrainCarriage1)){
                throw new BusinessException(BusinessExceptionEnum.DAILY_TRAIN_CARRIAGE_EXIST);
            }

            dailyTrainCarriage.setId(SnowUtil.getSnowTime());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);

        }else{
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateById(dailyTrainCarriage);
        }

    }

    public DailyTrainCarriage queryBuUnique(Date date, String trainCode, Integer index){
        LambdaQueryWrapper<DailyTrainCarriage> eq = new LambdaQueryWrapper<DailyTrainCarriage>()
                .eq(DailyTrainCarriage::getDate, date)
                .eq(DailyTrainCarriage::getTrainCode, trainCode)
                .eq(DailyTrainCarriage::getIndex, index);
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageMapper.selectList(eq);
        if(CollUtil.isNotEmpty(dailyTrainCarriages)){
            return dailyTrainCarriages.get(0);
        }
        return null;
    }

    @Transactional
    public void genDailyStation(Date date, String code) {
        LambdaQueryWrapper<DailyTrainCarriage> eq = new LambdaQueryWrapper<DailyTrainCarriage>()
                .eq(DailyTrainCarriage::getDate, date)
                .eq(DailyTrainCarriage::getTrainCode, code);
        dailyTrainCarriageMapper.delete(eq);

        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(code);
        if(CollUtil.isEmpty(trainCarriages)){
            log.info("没有该车次的基础车厢数据，生成车次车厢结束");
            return;
        }

        for (TrainCarriage trainCarriage : trainCarriages) {
            DateTime now = DateTime.now();

            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);

            dailyTrainCarriage.setId(SnowUtil.getSnowTime());
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);

            log.info("生成车次编号为【{}】的【{}】号车厢"
                    ,code,dailyTrainCarriage.getIndex());
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }
    }

    public List<DailyTrainCarriage> selectBySeatType(Date date, String trainCode, String seatType){
        LambdaQueryWrapper<DailyTrainCarriage> eq = new LambdaQueryWrapper<DailyTrainCarriage>()
                .eq(DailyTrainCarriage::getDate, DateUtil.beginOfDay(date))
                .eq(DailyTrainCarriage::getTrainCode, trainCode)
                .eq(DailyTrainCarriage::getSeatType, seatType);

        return dailyTrainCarriageMapper.selectList(eq);
    }
}




