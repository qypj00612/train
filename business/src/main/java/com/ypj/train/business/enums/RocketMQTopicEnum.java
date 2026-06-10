package com.ypj.train.business.enums;

import lombok.Getter;

@Getter
public enum RocketMQTopicEnum {
    CONFIRM_ORDER("CONFIRM_ORDER","排队购票");
    final String code;
    final String desc;

    RocketMQTopicEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
