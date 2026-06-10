package com.ypj.train.business.enums;

import lombok.Getter;

@Getter
public enum RedisPreEnum {
    SK_COUNT("SK_COUNT","令牌数"),
    SK_LOCK("SK_LOCK","令牌锁"),
    ORDER_LOCK("ORDER_LOCK","购票锁");

    final String desc;
    final String code;

    RedisPreEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }
}
