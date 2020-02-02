package com.seckill.controller.vos;

import lombok.Data;
import org.joda.time.DateTime;

import javax.annotation.security.DenyAll;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Stephen Jia
 * @create 2020-01-31   10:24
 */
@Data
public class ItemVO {
    private Integer id;
    //商品名
    private String title;
    //商品价格
    private BigDecimal price;
    //商品库存
    private Integer stock;
    //商品描述
    private String description;
    //商品销量
    private Integer sales;
    //商品描述图片的url
    private String imgUrl;

    //记录商品是否在秒杀活动中，以及对应的状态，
    //为0：表示没有秒杀活动
    //为1：表示秒杀活动进未开始
    //为2：表示秒杀进行中
    private Integer killStatus;

    //秒杀活动的优惠价
    private BigDecimal killPrice;
    //秒杀活动的id
    private Integer killId;
    //秒杀活动开始时间
    private String startDate;
}
