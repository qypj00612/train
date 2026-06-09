package com.ypj.train.member.req;

import com.ypj.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TicketQueryReq extends PageReq {
    private Long memberId;
}
