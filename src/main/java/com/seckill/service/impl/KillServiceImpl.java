package com.seckill.service.impl;

import com.seckill.dao.KillDOMapper;
import com.seckill.dos.KillDO;
import com.seckill.service.KillService;
import com.seckill.service.bos.KillBO;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Stephen Jia
 * @create 2020-02-02   9:52
 */
@Service
public class KillServiceImpl implements KillService {
    @Autowired
    private KillDOMapper killDOMapper;
    @Override
    public KillBO getKillByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        KillDO killDO = killDOMapper.selectByItemId(itemId);
        //dataObject--->businessModel
        KillBO killBO = convertFromKillDoToBo(killDO);
        if(killBO == null){
            return null;
        }
        //判断当前时间是否秒杀活动即将开始或者正在进行
        DateTime now = new DateTime();
        if(killBO.getStartDate().isAfter(now)){
            //秒杀还未开始
            killBO.setStatus(1);
        }else if (killBO.getEndDate().isBefore(now)){
            //秒杀已经结束
            killBO.setStatus(3);
        }else{
            killBO.setStatus(2);
        }

        return killBO;
    }


    private KillBO convertFromKillDoToBo(KillDO killDO){
        if(killDO == null){
            return null;
        }
        KillBO killBO = new KillBO();
        BeanUtils.copyProperties(killDO,killBO);
        killBO.setStartDate(new DateTime(killDO.getStartDate()));
        killBO.setEndDate(new DateTime(killDO.getEndDate()));
        return killBO;
    }
}
