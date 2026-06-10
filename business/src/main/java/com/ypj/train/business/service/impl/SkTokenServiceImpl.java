package com.ypj.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.SkToken;
import com.ypj.train.business.enums.RedisPreEnum;
import com.ypj.train.business.mapper.SkTokenMapper;
import com.ypj.train.business.req.SkTokenQueryReq;
import com.ypj.train.business.req.SkTokenSaveReq;
import com.ypj.train.business.resp.SkTokenQueryResp;
import com.ypj.train.business.service.SkTokenService;
import com.ypj.train.common.resp.PageResp;
import com.ypj.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author Ypj
* @description 针对表【sk_token(秒杀令牌)】的数据库操作Service实现
* @createDate 2026-03-01 14:23:00
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class SkTokenServiceImpl extends ServiceImpl<SkTokenMapper, SkToken>
    implements SkTokenService{

    private final SkTokenMapper skTokenMapper;

    private final DailyTrainSeatServiceImpl dailyTrainSeatServiceImpl;

    private final DailyTrainStationServiceImpl dailyTrainStationServiceImpl;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(SkTokenSaveReq skTokenSaveReq) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(skTokenSaveReq, SkToken.class);
        if(ObjectUtil.isNull(skToken.getId())){
            Long id = SnowUtil.getSnowTime();
            skToken.setId(id);
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        }else{
            skTokenMapper.updateById(skToken);
        }
    }

    @Override
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq skTokenQueryReq) {
        Page<SkToken> page = new Page<>(skTokenQueryReq.getPage(), skTokenQueryReq.getPageSize());
        QueryWrapper<SkToken> skTokenQueryWrapper = new QueryWrapper<>();
        skTokenMapper.selectPage(page, skTokenQueryWrapper);

        List<SkToken> skTokens = page.getRecords();
        List<SkTokenQueryResp> skTokenQueryResps = BeanUtil.copyToList(skTokens, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setRows(skTokenQueryResps);
        pageResp.setTotal(page.getTotal());
        return pageResp;
    }

    public void generateSkToken(Date date, String trainCode){
        log.info("删除[{}]车次[{}]的令牌记录", DateUtil.formatDate(date), trainCode);

        LambdaUpdateWrapper<SkToken> eq = new LambdaUpdateWrapper<SkToken>()
                .eq(SkToken::getDate, date)
                .eq(SkToken::getTrainCode, trainCode);
        skTokenMapper.delete(eq);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setId(SnowUtil.getSnowTime());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        Integer seatCount = dailyTrainSeatServiceImpl.countSeat(date, trainCode);
        log.info("生成令牌的座位数为[{}]",seatCount);
        Integer stationCount = dailyTrainStationServiceImpl.countStation(date, trainCode);
        log.info("生成令牌的站数为[{}]",stationCount);
        //实际售出的票数小于座位数*站数, 具体的令牌数需要根据卖票比例来算, *3/4仅为模拟
        Integer count = (Integer) seatCount*stationCount*3/4;
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    public boolean valid(Date date, String trainCode, Long memberId) {
        String lockKey = RedisPreEnum.SK_LOCK.getDesc() + DateUtil.formatDate(date) + "_" + trainCode + "_" + memberId;
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if(Boolean.FALSE.equals(b)){
            return false;
        }
        log.info("获取到令牌锁");
        String skCount = RedisPreEnum.SK_COUNT.getDesc() + DateUtil.formatDate(date) + "_" + trainCode;
        Object s = stringRedisTemplate.opsForValue().get(skCount);
        if(ObjectUtil.isNotNull(s)){
            Long count = stringRedisTemplate.opsForValue().decrement(skCount,1);
            if(count<1L){
                log.info("令牌失效");
                return false;
            }
            log.info("剩余令牌数: {}",count);
            stringRedisTemplate.expire(skCount,60,TimeUnit.SECONDS);
            if(count%5==0){
                log.info("更新数据库");
                skTokenMapper.decrease(date,trainCode,5);
            }
            return true;
        }else{
            LambdaQueryWrapper<SkToken> eq = new LambdaQueryWrapper<SkToken>()
                    .eq(SkToken::getDate, DateUtil.formatDate(date))
                    .eq(SkToken::getTrainCode, trainCode);
            List<SkToken> skTokens = skTokenMapper.selectList(eq);
            if(CollUtil.isEmpty(skTokens)){
                return false;
            }
            SkToken skToken = skTokens.get(0);
            Integer count = skToken.getCount()-1;
            stringRedisTemplate.opsForValue().set(skCount, String.valueOf(count));
            stringRedisTemplate.expire(skCount,60,TimeUnit.SECONDS);
            return true;
        }
    }
}




