package com.ypj.train.business.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ConfirmOrderMQDTO {

    private String logId;

    private Date date;

    private String trainCode;
}
