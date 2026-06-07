package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.TrainCarriage;
import com.ypj.train.business.domain.TrainSeat;
import com.ypj.train.business.enums.SeatColEnum;
import com.ypj.train.business.mapper.TrainCarriageMapper;
import com.ypj.train.business.mapper.TrainSeatMapper;
import com.ypj.train.business.req.TrainSeatQueryReq;
import com.ypj.train.business.resp.TrainSeatQueryResp;
import com.ypj.train.business.service.TrainSeatService;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Ypj
* @description 针对表【train_seat(座位)】的数据库操作Service实现
* @createDate 2025-11-24 20:26:55
*/
@Service
@RequiredArgsConstructor
public class TrainSeatServiceImpl extends ServiceImpl<TrainSeatMapper, TrainSeat>
    implements TrainSeatService{

    private final TrainSeatMapper trainSeatMapper;
    private final TrainCarriageMapper trainCarriageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSeat(String code) {
        DateTime now = DateTime.now();

        // 清空当前车次下的所有座位数据
        LambdaQueryWrapper<TrainSeat> eq = new LambdaQueryWrapper<TrainSeat>()
                .eq(TrainSeat::getTrainCode, code);
        trainSeatMapper.delete(eq);

        // 查找当前车次下的所有车厢
        LambdaQueryWrapper<TrainCarriage> eq1 = new LambdaQueryWrapper<TrainCarriage>()
                .eq(TrainCarriage::getTrainCode, code);
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectList(eq1);

        // 循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : trainCarriages) {
            int count = 1;
            Integer rowCount = trainCarriage.getRowCount();
            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(trainCarriage.getSeatType());

            // 循环行数
            for (Integer i=1; i<=rowCount; i++) {
                // 循环列数
                for (SeatColEnum seatColEnum : colsByType) {
                    // 构造座位数据
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowTime());
                    trainSeat.setTrainCode(code);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(i.toString(),'0',2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatColEnum.getType());
                    trainSeat.setCarriageSeatIndex(count++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);

                    // 将数据存入数据库
                    trainSeatMapper.insert(trainSeat);
                }
            }

        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        Page<TrainSeat> trainSeatPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<TrainSeat> trainSeatLambdaQueryWrapper = new LambdaQueryWrapper<TrainSeat>()
                .orderByAsc(TrainSeat::getTrainCode)
                .orderByAsc(TrainSeat::getCarriageIndex)
                .orderByAsc(TrainSeat::getCarriageSeatIndex);

        if(StrUtil.isNotBlank(req.getTrainCode())){
            trainSeatLambdaQueryWrapper.eq(TrainSeat::getTrainCode, req.getTrainCode());
        }

        trainSeatMapper.selectPage(trainSeatPage,trainSeatLambdaQueryWrapper);

        List<TrainSeat> trainSeatList = trainSeatPage.getRecords();
        List<TrainSeatQueryResp> trainSeatQueryResps = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);

        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainSeatPage.getTotal());
        pageResp.setRows(trainSeatQueryResps);
        return pageResp;
    }

    public List<TrainSeat> selectByTrainCode(String trainCode) {
        LambdaQueryWrapper<TrainSeat> eq = new LambdaQueryWrapper<TrainSeat>()
                .eq(TrainSeat::getTrainCode, trainCode);
        return trainSeatMapper.selectList(eq);
    }
}




