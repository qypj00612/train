package com.ypj.train.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.member.domain.Passenger;
import com.ypj.train.member.req.PassengerQueryReq;
import com.ypj.train.member.req.PassengerSaveReq;
import com.ypj.train.member.resp.PassengerQueryResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【passenger(乘车人)】的数据库操作Service
* @createDate 2025-11-19 18:01:42
*/
public interface PassengerService extends IService<Passenger> {

    void save(PassengerSaveReq passengerSaveReq);

    PageResp<PassengerQueryResp> queryList(PassengerQueryReq passengerQueryReq);

    List<PassengerQueryResp> queryMine();
}
