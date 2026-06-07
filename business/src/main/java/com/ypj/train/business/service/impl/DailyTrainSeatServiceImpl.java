package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.DailyTrainSeat;
import com.ypj.train.business.mapper.DailyTrainSeatMapper;
import com.ypj.train.business.req.DailyTrainSeatQueryReq;
import com.ypj.train.business.resp.DailyTrainSeatQueryResp;
import com.ypj.train.business.service.DailyTrainSeatService;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【daily_train_seat(每日座位)】的数据库操作Service实现
* @createDate 2025-11-27 19:37:17
*/
@Service
@RequiredArgsConstructor
public class DailyTrainSeatServiceImpl extends ServiceImpl<DailyTrainSeatMapper, DailyTrainSeat>
    implements DailyTrainSeatService{

    private final DailyTrainSeatMapper dailyTrainSeatMapper;

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        Page<DailyTrainSeat> dailyTrainSeatPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<DailyTrainSeat> dailyTrainSeatLambdaQueryWrapper = new LambdaQueryWrapper<DailyTrainSeat>()
                .orderByDesc(DailyTrainSeat::getDate)
                .orderByAsc(DailyTrainSeat::getTrainCode)
                .orderByAsc(DailyTrainSeat::getCarriageIndex)
                .orderByAsc(DailyTrainSeat::getCarriageSeatIndex);

        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            dailyTrainSeatLambdaQueryWrapper.eq(DailyTrainSeat::getTrainCode, req.getTrainCode());
        }

        dailyTrainSeatMapper.selectPage(dailyTrainSeatPage,dailyTrainSeatLambdaQueryWrapper);

        List<DailyTrainSeat> records = dailyTrainSeatPage.getRecords();
        List<DailyTrainSeatQueryResp> dailyTrainSeatQueryResps = BeanUtil.copyToList(records, DailyTrainSeatQueryResp.class);
        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainSeatPage.getTotal());
        pageResp.setRows(dailyTrainSeatQueryResps);
        return pageResp;
    }
}




