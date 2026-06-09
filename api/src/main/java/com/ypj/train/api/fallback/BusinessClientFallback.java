package com.ypj.train.api.fallback;

import com.ypj.train.api.client.BusinessClient;
import com.ypj.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BusinessClientFallback implements BusinessClient {
    @Override
    public CommonResp<Object> genDaily(Date data) {
        return new CommonResp<>("fallBack");
    }

    @Override
    public CommonResp<String> hello() {
        return null;
    }
}
