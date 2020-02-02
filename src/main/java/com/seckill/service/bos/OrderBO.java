package com.seckill.service.bos;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Stephen Jia
 * @create 2020-02-01   9:08
 */
@Data
public class OrderBO {
    //订单模型的id
    private String id;
    //下单用户的id
    private Integer userId;
    //商品的id
    private Integer itemId;
    //商品的价格,价格取决于，killId是否为空
    private BigDecimal itemPrice;
    //下单的商品的数量
    private Integer amount;
    //该订单的总价,价格取决于，killId是否为空
    private BigDecimal orderPrice;

    //若非空，表示是以秒杀商品方式下单
    private Integer killId;

}
