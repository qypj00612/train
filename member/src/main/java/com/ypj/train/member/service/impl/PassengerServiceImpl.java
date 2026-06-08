package com.ypj.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.common.context.MemberContext;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import com.ypj.train.member.domain.Passenger;
import com.ypj.train.member.mapper.PassengerMapper;
import com.ypj.train.member.req.PassengerQueryReq;
import com.ypj.train.member.req.PassengerSaveReq;
import com.ypj.train.member.resp.PassengerQueryResp;
import com.ypj.train.member.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【passenger(乘车人)】的数据库操作Service实现
* @createDate 2025-11-19 18:01:42
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger>
    implements PassengerService{

    private final PassengerMapper passengerMapper;

    @Override
    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq passengerQueryReq) {
        Page<Passenger> page = new Page<>(passengerQueryReq.getPage(), passengerQueryReq.getPageSize());

        log.info("查询页码: {}", passengerQueryReq.getPage());
        log.info("页码大小: {}", passengerQueryReq.getPageSize());

        LambdaQueryWrapper<Passenger> eq = new LambdaQueryWrapper<Passenger>()
                .eq(Passenger::getMemberId, passengerQueryReq.getMemberId());
        passengerMapper.selectPage(page, eq);

        log.info("总页数: {}", page.getPages());
        log.info("总行数: {}", page.getTotal());

        List<Passenger> records = page.getRecords();
        List<PassengerQueryResp> passengerQueryResps = BeanUtil.copyToList(records, PassengerQueryResp.class);

        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(page.getTotal());
        pageResp.setRows(passengerQueryResps);
        return pageResp;
    }

    @Override
    public List<PassengerQueryResp> queryMine() {
        LambdaQueryWrapper<Passenger> eq = new LambdaQueryWrapper<Passenger>()
                .eq(Passenger::getMemberId, MemberContext.getId());

        List<Passenger> passengers = passengerMapper.selectList(eq);
        return BeanUtil.copyToList(passengers, PassengerQueryResp.class);
    }

    @Override
    public void save(PassengerSaveReq passengerSaveReq) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(passengerSaveReq, Passenger.class);
        if(ObjectUtil.isNull(passenger.getId())) {
            passenger.setMemberId(MemberContext.getId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passenger.setId(SnowUtil.getSnowTime());
            passengerMapper.insert(passenger);
        }else{
            passenger.setUpdateTime(now);
            passengerMapper.updateById(passenger);
        }
    }
}




