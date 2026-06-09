package com.ypj.train.api.client;

import com.ypj.train.api.fallback.BusinessClientFallback;
import com.ypj.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

@FeignClient(value = "business", fallback = BusinessClientFallback.class)
public interface BusinessClient {
    @GetMapping("admin/daily-train/gen-daily/{data}")
    CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date data);

    @GetMapping("business/confirm-order/hello")
    CommonResp<String> hello();
}
