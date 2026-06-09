package com.ypj.train.common.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TicketQueryReq extends PageReq {
    private Long memberId;
}
