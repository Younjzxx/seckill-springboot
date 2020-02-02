package com.seckill.service;

import com.seckill.service.bos.KillBO;

/**
 * @author Stephen Jia
 * @create 2020-02-02   9:49
 */
public interface KillService {
    //根据itemId获取即将进行的或者正在进行的秒杀活动
    KillBO getKillByItemId(Integer itemId);
}
