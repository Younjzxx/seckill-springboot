package com.seckill.service.impl;

import com.seckill.dao.ItemDOMapper;
import com.seckill.dao.ItemStockDOMapper;
import com.seckill.dos.ItemDO;
import com.seckill.dos.ItemStockDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.ItemService;
import com.seckill.service.KillService;
import com.seckill.service.bos.ItemBO;
import com.seckill.service.bos.KillBO;
import com.seckill.validator.ValidationResult;
import com.seckill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stephen Jia
 * @create 2020-01-31   9:33
 */
@Service
public class ItemServiceImpl implements ItemService {


    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private KillService killService;


    @Override
    @Transactional
    public ItemBO createItem(ItemBO itemBO) throws BusinessException {
        //校验参数
        ValidationResult result = validator.validate(itemBO);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //转化 itembo to itemdo
        ItemDO itemDO = this.convertFromItemBoToItemDo(itemBO);
        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemBO.setId(itemDO.getId());

        ItemStockDO itemStockDO = this.convertFromItemBOtoItemStockDO(itemBO);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemBO.getId());
    }

    @Override
    public List<ItemBO> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        //使用stream api将list里面的每一个do对象转化为bo
        List<ItemBO> itemBOList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemBO itemBO = this.convertFromTwoDOsToBo(itemDO, itemStockDO);
            return itemBO;
        }).collect(Collectors.toList());
        return itemBOList;
    }

    @Override
    public ItemBO getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        //将两个do转化为bo
        ItemBO itemBO = convertFromTwoDOsToBo(itemDO, itemStockDO);
        //获取活动商品信息
        KillBO killBO = killService.getKillByItemId(id);
        if(killBO != null && killBO.getStatus().intValue() != 3){
            //聚合秒杀模型到商品模型
            itemBO.setKillBO(killBO);
        }
        return itemBO;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        if(affectedRow > 0){
            //更新库存成功
            return true;
        }else{
            //更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId, amount);
    }


    //将两个do转化为itmobo
    private ItemBO convertFromTwoDOsToBo(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemBO itemBO = new ItemBO();
        BeanUtils.copyProperties(itemDO, itemBO);
        itemBO.setStock(itemStockDO.getStock());
        return itemBO;
    }


    //业务对象转化为数据库对象以便插入数据库
    private ItemDO convertFromItemBoToItemDo(ItemBO itemBO){
        if(itemBO == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemBO, itemDO);
        return itemDO;
    }

    //业务对象转化为库存的数据库对象，即把bo里面的库存itemid取出来
    private ItemStockDO convertFromItemBOtoItemStockDO(ItemBO itemBO){
        if(itemBO == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemBO.getId());
        itemStockDO.setStock(itemBO.getStock());
        return itemStockDO;
    }
}
