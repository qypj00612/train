package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.DailyTrainCarriage;
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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Ypj
* @description 针对表【daily_train_carriage(每日车厢)】的数据库操作Service实现
* @createDate 2025-11-26 21:57:27
*/
@Service
@RequiredArgsConstructor
public class DailyTrainCarriageServiceImpl extends ServiceImpl<DailyTrainCarriageMapper, DailyTrainCarriage>
    implements DailyTrainCarriageService{

    private final DailyTrainCarriageMapper dailyTrainCarriageMapper;

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
}




