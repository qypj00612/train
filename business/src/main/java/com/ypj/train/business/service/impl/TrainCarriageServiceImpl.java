package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.TrainCarriage;
import com.ypj.train.business.enums.SeatColEnum;
import com.ypj.train.business.mapper.TrainCarriageMapper;
import com.ypj.train.business.req.TrainCarriageQueryReq;
import com.ypj.train.business.req.TrainCarriageSaveReq;
import com.ypj.train.business.resp.TrainCarriageQueryResp;
import com.ypj.train.business.service.TrainCarriageService;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【train_carriage(火车车厢)】的数据库操作Service实现
* @createDate 2025-11-24 19:41:07
*/
@RequiredArgsConstructor
@Service
public class TrainCarriageServiceImpl extends ServiceImpl<TrainCarriageMapper, TrainCarriage>
    implements TrainCarriageService{

    private final TrainCarriageMapper trainCarriageMapper;

    @Override
    public void save(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();

        List<SeatColEnum> colsByType = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(colsByType.size());
        req.setSeatCount(req.getRowCount()*colsByType.size());

        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if(ObjectUtil.isNull(trainCarriage.getId())){

            TrainCarriage trainCarriage1 = selectByUnique(trainCarriage.getTrainCode(), trainCarriage.getIndex());
            if(ObjectUtil.isNotNull(trainCarriage1)){
                throw new BusinessException(BusinessExceptionEnum.TRAIN_CARRIAGE_EXIST);
            }

            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriage.setId(SnowUtil.getSnowTime());
            trainCarriageMapper.insert(trainCarriage);
        }else{
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateById(trainCarriage);
        }
    }

    @Override
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        Page<TrainCarriage> trainCarriagePage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<TrainCarriage> trainCarriageLambdaQueryWrapper = new LambdaQueryWrapper<TrainCarriage>()
                .orderByAsc(TrainCarriage::getTrainCode)
                .orderByAsc(TrainCarriage::getIndex);

        if(StrUtil.isNotBlank(req.getTrainCode())){
            trainCarriageLambdaQueryWrapper.eq(TrainCarriage::getTrainCode, req.getTrainCode());
        }

        trainCarriageMapper.selectPage(trainCarriagePage,trainCarriageLambdaQueryWrapper);

        List<TrainCarriage> trainCarriageList = trainCarriagePage.getRecords();
        List<TrainCarriageQueryResp> trainCarriageQueryResps = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);

        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainCarriagePage.getTotal());
        pageResp.setRows(trainCarriageQueryResps);
        return pageResp;
    }

    public TrainCarriage selectByUnique(String trainCode,Integer index) {
        LambdaQueryWrapper<TrainCarriage> eq = new LambdaQueryWrapper<TrainCarriage>()
                .eq(TrainCarriage::getTrainCode, trainCode)
                .eq(TrainCarriage::getIndex, index);
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectList(eq);
        if(CollUtil.isNotEmpty(trainCarriages)){
            return trainCarriages.get(0);
        }
        return null;
    }

    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        LambdaQueryWrapper<TrainCarriage> eq = new LambdaQueryWrapper<TrainCarriage>()
                .eq(TrainCarriage::getTrainCode, trainCode);
        return trainCarriageMapper.selectList(eq);
    }

}




