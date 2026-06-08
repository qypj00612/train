package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.ConfirmOrder;
import com.ypj.train.business.mapper.ConfirmOrderMapper;
import com.ypj.train.business.req.ConfirmOrderQueryReq;
import com.ypj.train.business.resp.ConfirmOrderQueryResp;
import com.ypj.train.business.service.ConfirmOrderService;
import com.ypj.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【confirm_order(确认订单)】的数据库操作Service实现
* @createDate 2025-12-02 21:48:22
*/
@Service
@RequiredArgsConstructor
public class ConfirmOrderServiceImpl extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder>
    implements ConfirmOrderService{

    private final ConfirmOrderMapper confirmOrderMapper;

    @Override
    public PageResp<ConfirmOrderQueryResp> query(ConfirmOrderQueryReq req) {
        Page<ConfirmOrder> confirmOrderPage = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<ConfirmOrder> confirmOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();

        confirmOrderMapper.selectPage(confirmOrderPage,confirmOrderLambdaQueryWrapper);

        List<ConfirmOrder> records = confirmOrderPage.getRecords();
        List<ConfirmOrderQueryResp> confirmOrderQueryResps = BeanUtil.copyToList(records, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> resp = new PageResp<>();
        resp.setTotal(confirmOrderPage.getTotal());
        resp.setRows(confirmOrderQueryResps);

        return resp;
    }
}




