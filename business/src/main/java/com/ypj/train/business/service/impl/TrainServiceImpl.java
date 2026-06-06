package com.ypj.train.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypj.train.business.domain.Train;
import com.ypj.train.business.service.TrainService;
import com.ypj.train.business.mapper.TrainMapper;
import org.springframework.stereotype.Service;

/**
* @author Ypj
* @description 针对表【train(车次)】的数据库操作Service实现
* @createDate 2025-11-20 21:26:12
*/
@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train>
    implements TrainService{

}




