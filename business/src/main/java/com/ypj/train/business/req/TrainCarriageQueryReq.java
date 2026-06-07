package com.ypj.train.business.req;

import com.ypj.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainCarriageQueryReq extends PageReq {
    private String trainCode;
}
