package com.ypj.train.member.enums;

public enum PassengerEnum {
    ADULT(1,"成人"),
    CHILE(2,"儿童"),
    STUDENT(3,"学生");

    private int code;
    private String des;

    PassengerEnum(int code, String des) {
        this.code = code;
        this.des = des;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return "PassengerEnum{" +
                "code=" + code +
                ", des='" + des + '\'' +
                '}';
    }
}
