package com.ypj.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class StationSaveReq {

    private Long id;

    @NotBlank(message = "站名不能为空")
    private String name;

    @NotBlank(message = "站名拼音不能为空")
    private String namePinyin;

    @NotBlank(message = "站名拼音首字母不能为空")
    private String namePy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
}
