package com.ypj.train.business.service;

import com.ypj.train.business.req.ConfirmOrderDoReq;

public interface BeforeConfirmOrder {
    Long beforeDoConfirm(ConfirmOrderDoReq req);
}
