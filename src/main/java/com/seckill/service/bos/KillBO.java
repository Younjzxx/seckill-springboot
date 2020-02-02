package com.seckill.service.bos;

import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Stephen Jia
 * @create 2020-02-01   17:12
 */
@Data
public class KillBO {
    private Integer id;
    //秒杀活动的状态 1表示还未开始 2表示进行中 3表示已结束
    private Integer status;
    //秒杀活动名称
    private String killName;
    //秒杀开始时间(引入joda-time)
    private DateTime startDate;
    //秒杀结束时间
    private DateTime endDate;
    //秒杀活动的使用商品
    private Integer itemId;
    //秒杀活动的商品价格
    private BigDecimal killItemPrice;
}
