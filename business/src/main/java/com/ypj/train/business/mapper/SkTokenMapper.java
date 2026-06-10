package com.ypj.train.business.mapper;

import com.ypj.train.business.domain.SkToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;

/**
* @author Ypj
* @description 针对表【sk_token(秒杀令牌)】的数据库操作Mapper
* @createDate 2026-03-01 14:23:00
* @Entity com.ypj.train.business.domain.SkToken
*/
public interface SkTokenMapper extends BaseMapper<SkToken> {

    int decrease(Date date, String trainCode);
}




