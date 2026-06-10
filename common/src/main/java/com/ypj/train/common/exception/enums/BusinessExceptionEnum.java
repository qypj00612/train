package com.ypj.train.common.exception.enums;

public enum BusinessExceptionEnum {
    MOBILE_EXIST("手机号被注册"),
    MOBILE_NOT_EXIST("请发送短信验证码"),
    CODE_ERROR("短信验证错误"),

    STATION_EXIST("车站名已存在"),
    TRAIN_CARRIAGE_EXIST("车厢已存在"),
    TRAIN_EXIST("车次已存在"),
    TRAIN_STATION_EXIST("车站已存在"),

    DAILY_TRAIN_EXIST("今日车次已存在"),
    DAILY_TRAIN_STATION_EXIST("今日车站已存在"),
    DAILY_TRAIN_CARRIAGE_EXIST("今日车次车厢已存在"),

    CONFiRM_ORDER_TICKET_ERROR("余票数量不足"),

    TICKET_EXCEPTION("服务繁忙, 请稍后重试"),
    TICKET_EXCEPTION_LOCK("抢票人数过多, 请稍后重试"),
    TICKET_EXCEPTION_FLOW("抢票人数太多了, 请稍后重试"),

    CONFIRM_ORDER_SK_FALSE("票已卖光");

    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

    public boolean equals(BusinessExceptionEnum businessExceptionEnum) {
        return desc.equals(businessExceptionEnum.getDesc());
    }
}
