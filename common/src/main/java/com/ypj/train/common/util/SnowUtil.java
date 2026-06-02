package com.ypj.train.common.util;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {

    private static long dataCenterId=1; // 数据中心
    private static long workId=1; // 机器标识

    public static long getSnowTime(){
        return IdUtil.getSnowflake(workId, dataCenterId).nextId();
    }

    public static String getSnowTimeStr(){
        return IdUtil.getSnowflake(workId, dataCenterId).nextIdStr();
    }

}
