package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.TrainStation;
import com.ypj.train.business.mapper.TrainStationMapper;
import com.ypj.train.business.req.TrainStationQueryReq;
import com.ypj.train.business.req.TrainStationSaveReq;
import com.ypj.train.business.resp.TrainStationResp;
import com.ypj.train.business.service.TrainStationService;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【train_station(火车车站)】的数据库操作Service实现
* @createDate 2025-11-23 18:49:52
*/
@Service
@RequiredArgsConstructor
public class TrainStationServiceImpl extends ServiceImpl<TrainStationMapper, TrainStation>
    implements TrainStationService{

    private final TrainStationMapper trainStationMapper;

    @Override
    public PageResp<TrainStationResp> queryTrainStationList(TrainStationQueryReq req) {
        Page<TrainStation> trainStationPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<TrainStation> trainStationLambdaQueryWrapper = new LambdaQueryWrapper<TrainStation>()
                .orderByAsc(TrainStation::getTrainCode)
                .orderByAsc(TrainStation::getIndex);

        if(StrUtil.isNotBlank(req.getTrainCode())){
            trainStationLambdaQueryWrapper.eq(TrainStation::getTrainCode, req.getTrainCode());
        }

        trainStationMapper.selectPage(trainStationPage, trainStationLambdaQueryWrapper);
        List<TrainStation> trainStationList = trainStationPage.getRecords();

        List<TrainStationResp> trainStationResps = BeanUtil.copyToList(trainStationList, TrainStationResp.class);
        PageResp<TrainStationResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainStationPage.getTotal());
        pageResp.setRows(trainStationResps);
        return pageResp;
    }

    @Override
    public void save(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if(ObjectUtil.isNull(trainStation.getId())){

            TrainStation trainStation1 = queryByUnique(req.getTrainCode(), req.getIndex(), req.getName());
            if(ObjectUtil.isNotNull(trainStation1)){
                throw new BusinessException(BusinessExceptionEnum.TRAIN_STATION_EXIST);
            }

            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStation.setId(SnowUtil.getSnowTime());
            trainStationMapper.insert(trainStation);
        }else{
            trainStation.setUpdateTime(now);
            trainStationMapper.updateById(trainStation);
        }
    }

    public TrainStation queryByUnique(String trainCode, Integer index, String name){
        LambdaQueryWrapper<TrainStation> eq = new LambdaQueryWrapper<TrainStation>()
                .eq(TrainStation::getTrainCode, trainCode)
                .eq(TrainStation::getIndex, index)
                .or()
                .eq(TrainStation::getName, name)
                .eq(TrainStation::getTrainCode, trainCode);
        List<TrainStation> trainStations = trainStationMapper.selectList(eq);
        if(CollUtil.isNotEmpty(trainStations)){
            return trainStations.get(0);
        }
        return null;
    }

    public List<TrainStation> selectByTrainCode(String trainCode){
        LambdaQueryWrapper<TrainStation> eq = new LambdaQueryWrapper<TrainStation>()
                .eq(TrainStation::getTrainCode, trainCode);
        return trainStationMapper.selectList(eq);
    }

}




