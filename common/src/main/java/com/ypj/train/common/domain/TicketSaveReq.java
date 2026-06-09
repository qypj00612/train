package com.ypj.train.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class TicketSaveReq {
    /**
     * id
     */
    private Long id;

    /**
     * 会员id
     */
    @NotNull(message = "[]不能为空")
    private Long memberId;

    /**
     * 乘客id
     */
    @NotNull(message = "[]不能为空")
    private Long passengerId;

    /**
     * 乘客姓名
     */
    @NotBlank(message = "[]不能为空")
    private String passengerName;

    /**
     * 日期
     */
    @NotNull(message = "[]不能为空")
    private Date date;

    /**
     * 车次编号
     */
    @NotBlank(message = "[]不能为空")
    private String trainCode;

    /**
     * 箱序
     */
    @NotNull(message = "[]不能为空")
    private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
    @NotBlank(message = "[]不能为空")
    private String row;

    /**
     * 列号|枚举[SeatColEnum]
     */
    @NotBlank(message = "[]不能为空")
    private String col;

    /**
     * 出发站
     */
    @NotBlank(message = "[]不能为空")
    private String start;

    /**
     * 出发时间
     */
    @NotNull(message = "[]不能为空")
    private Date startTime;

    /**
     * 到达站
     */
    @NotBlank(message = "[]不能为空")
    private String end;

    /**
     * 到站时间
     */
    @NotNull(message = "[]不能为空")
    private Date endTime;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @NotBlank(message = "[]不能为空")
    private String seatType;

    /**
     * 新增时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
