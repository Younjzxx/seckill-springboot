package com.seckill.service;

import com.seckill.error.BusinessException;
import com.seckill.service.bos.ItemBO;

import java.util.List;

/**
 * @author Stephen Jia
 * @create 2020-01-31   9:30
 */
public interface ItemService {
    //创建商品
    ItemBO createItem(ItemBO itemBO) throws BusinessException;
    //商品列表浏览
    List<ItemBO> listItem();
    //商品详情浏览
    ItemBO getItemById(Integer id);
    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;
    //商品销量增加
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;
}
