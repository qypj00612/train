package com.ypj.train.business.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/redis")
public class RedisController {

    private final RedisTemplate redisTemplate;

    @RequestMapping("set/{key}/{value}")
    public String set(@PathVariable("key") String key, @PathVariable("value") String value) {
        redisTemplate.opsForValue().set(key, value, 1, TimeUnit.MINUTES);
        return "success";
    }

    @RequestMapping("get/{key}")
    public Object get(@PathVariable("key") String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
