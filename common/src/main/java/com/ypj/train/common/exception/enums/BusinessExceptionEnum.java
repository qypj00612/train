package com.ypj.train.common.exception.enums;

public enum BusinessExceptionEnum {
    MOBILE_EXIST("手机号被注册"),
    MOBILE_NOT_EXIST("请发送短信验证码"),
    CODE_ERROR("短信验证错误"),

    STATION_EXIST("车站名已存在"),
    TRAIN_CARRIAGE_EXIST("车厢已存在"),
    TRAIN_EXIST("车次已存在"),
    TRAIN_STATION_EXIST("车站已存在");

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
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
