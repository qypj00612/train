package com.ypj.train.member.service;

import com.ypj.train.member.domain.Passenger;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.member.req.PassengerSaveReq;

/**
* @author Ypj
* @description 针对表【passenger(乘车人)】的数据库操作Service
* @createDate 2025-11-19 18:01:42
*/
public interface PassengerService extends IService<Passenger> {

    void save(PassengerSaveReq passengerSaveReq);
}
