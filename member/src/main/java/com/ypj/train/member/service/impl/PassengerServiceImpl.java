package com.ypj.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.common.util.SnowUtil;
import com.ypj.train.member.domain.Passenger;
import com.ypj.train.member.req.PassengerSaveReq;
import com.ypj.train.member.service.PassengerService;
import com.ypj.train.member.mapper.PassengerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author Ypj
* @description 针对表【passenger(乘车人)】的数据库操作Service实现
* @createDate 2025-11-19 18:01:42
*/
@Service
@RequiredArgsConstructor
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger>
    implements PassengerService{

    private final PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq passengerSaveReq) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(passengerSaveReq, Passenger.class);
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passenger.setId(SnowUtil.getSnowTime());
        passengerMapper.insert(passenger);
    }
}




