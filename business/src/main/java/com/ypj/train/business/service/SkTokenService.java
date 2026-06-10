package com.ypj.train.business.service;

import com.ypj.train.business.domain.SkToken;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ypj.train.business.req.SkTokenQueryReq;
import com.ypj.train.business.req.SkTokenSaveReq;
import com.ypj.train.business.resp.SkTokenQueryResp;
import com.ypj.train.common.resp.PageResp;

/**
* @author Ypj
* @description 针对表【sk_token(秒杀令牌)】的数据库操作Service
* @createDate 2026-03-01 14:23:00
*/
public interface SkTokenService extends IService<SkToken> {

    void save(SkTokenSaveReq skTokenSaveReq);

    PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq skTokenQueryReq);
}
