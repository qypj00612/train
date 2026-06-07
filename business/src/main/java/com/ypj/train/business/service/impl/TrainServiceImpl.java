package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.Train;
import com.ypj.train.business.mapper.TrainMapper;
import com.ypj.train.business.req.TrainQueryReq;
import com.ypj.train.business.req.TrainSaveReq;
import com.ypj.train.business.resp.TrainQueryResp;
import com.ypj.train.business.service.TrainService;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【train(车次)】的数据库操作Service实现
* @createDate 2025-11-20 21:26:12
*/
@Service
@RequiredArgsConstructor
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train>
    implements TrainService{

    private final TrainMapper trainMapper;

    @Override
    public void save(TrainSaveReq req) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if(ObjectUtil.isNull(train.getId())){

            Train train1 = queryByUnique(req.getCode());
            if(ObjectUtil.isNotNull(train1)){
                throw new BusinessException(BusinessExceptionEnum.TRAIN_EXIST);
            }

            train.setId(SnowUtil.getSnowTime());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        }else{
            train.setUpdateTime(now);
            trainMapper.updateById(train);
        }
    }

    @Override
    public List<TrainQueryResp> queryAll() {
        LambdaQueryWrapper<Train> trainLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Train> trains = trainMapper.selectList(trainLambdaQueryWrapper);
        List<TrainQueryResp> trainQueryResps = BeanUtil.copyToList(trains, TrainQueryResp.class);
        return trainQueryResps;
    }

    @Override
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        Page<Train> page = new Page<>(req.getPage(), req.getPageSize());
        QueryWrapper<Train> queryWrapper = new QueryWrapper<>();

        Page<Train> page1 = trainMapper.selectPage(page, queryWrapper);
        List<Train> records = page1.getRecords();
        List<TrainQueryResp> trainQueryResps = BeanUtil.copyToList(records, TrainQueryResp.class);
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(page1.getTotal());
        pageResp.setRows(trainQueryResps);
        return pageResp;
    }

    public Train queryByUnique(String code){
        LambdaQueryWrapper<Train> eq = new LambdaQueryWrapper<Train>().eq(Train::getCode, code);
        List<Train> trains = trainMapper.selectList(eq);
        if(CollUtil.isNotEmpty(trains)){
            return trains.get(0);
        }
        return null;
    }
}




