package com.ypj.train.business.service;

import com.ypj.train.business.req.ConfirmOrderDoReq;

public interface BeforeConfirmOrder {
    void beforeDoConfirm(ConfirmOrderDoReq req);
}
