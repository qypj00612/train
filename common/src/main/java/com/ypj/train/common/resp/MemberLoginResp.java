package com.ypj.train.common.resp;

import lombok.Data;

@Data
public class MemberLoginResp {
    private String mobile;
    private Long id;
    private String token;
}
