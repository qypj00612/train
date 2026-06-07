package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.Station;
import com.ypj.train.business.req.StationQueryReq;
import com.ypj.train.business.req.StationSaveReq;
import com.ypj.train.business.resp.StationQueryResp;
import com.ypj.train.business.service.StationService;
import com.ypj.train.business.mapper.StationMapper;
import com.ypj.train.common.exception.BusinessException;
import com.ypj.train.common.exception.enums.BusinessExceptionEnum;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【station(车站)】的数据库操作Service实现
* @createDate 2025-11-20 20:22:37
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class StationServiceImpl extends ServiceImpl<StationMapper, Station>
    implements StationService{

    private final StationMapper stationMapper;

    @Override
    public void save(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if(ObjectUtil.isNull(station.getId())){

            Station station1 = selectByUnique(req.getName());
            if(ObjectUtil.isNotNull(station1)){
                throw new BusinessException(BusinessExceptionEnum.STATION_EXIST);
            }

            station.setId(SnowUtil.getSnowTime());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            this.save(station);
        }else {
            station.setUpdateTime(now);
            this.updateById(station);
        }
    }

    @Override
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        Page<Station> page = new Page<>(req.getPage(),req.getPageSize());
        log.info("查询页码: {}", req.getPage());
        log.info("页码大小: {}", req.getPageSize());
        LambdaQueryWrapper<Station> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        stationMapper.selectPage(page, lambdaQueryWrapper);
        log.info("总条数: {}", page.getTotal());
        log.info("总页数: {}", page.getRecords());

        List<Station> records = page.getRecords();
        List<StationQueryResp> stationQueryResps = BeanUtil.copyToList(records, StationQueryResp.class);

        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setRows(stationQueryResps);
        pageResp.setTotal(page.getTotal());

        return pageResp;
    }

    @Override
    public List<StationQueryResp> queryAll() {
        LambdaQueryWrapper<Station> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Station> stations = stationMapper.selectList(lambdaQueryWrapper);
        return BeanUtil.copyToList(stations, StationQueryResp.class);
    }

    public Station selectByUnique(String unique) {
        LambdaQueryWrapper<Station> eq = new LambdaQueryWrapper<Station>()
                .eq(Station::getName, unique);
        List<Station> stations = stationMapper.selectList(eq);
        if(CollUtil.isNotEmpty(stations)){
            return stations.get(0);
        }
        return null;
    }
}




