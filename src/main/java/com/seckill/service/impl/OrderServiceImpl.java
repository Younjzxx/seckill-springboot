package com.seckill.service.impl;

import com.seckill.dao.OrderDOMapper;
import com.seckill.dao.SequenceDOMapper;
import com.seckill.dos.OrderDO;
import com.seckill.dos.SequenceDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.ItemService;
import com.seckill.service.OrderService;
import com.seckill.service.UserService;
import com.seckill.service.bos.ItemBO;
import com.seckill.service.bos.OrderBO;
import com.seckill.service.bos.UserBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Stephen Jia
 * @create 2020-02-01   9:33
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    OrderDOMapper orderDOMapper;
    @Autowired
    SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderBO createOrder(Integer userId, Integer itemId, Integer killId, Integer amount) throws BusinessException {
        //校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemBO itemBO = itemService.getItemById(itemId);
        if(itemBO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        UserBO userBO = userService.getUserById(userId);
        if(userBO == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        if(amount <= 0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        //校验活动信息
        if(killId != null){
            //校验对应活动是否存在这个使用商品
            if(killId.intValue() != itemBO.getKillBO().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            }else if(itemBO.getKillBO().getStatus().intValue() != 2){//校验活动是否进行中
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }


        }

        //落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //订单入库
        OrderBO orderBO = new OrderBO();
        orderBO.setUserId(userId);
        orderBO.setItemId(itemId);
        orderBO.setAmount(amount);
        orderBO.setKillId(killId);
        if(killId != null){
            orderBO.setItemPrice(itemBO.getKillBO().getKillItemPrice());
        }else{
            orderBO.setItemPrice(itemBO.getPrice());
        }
        orderBO.setOrderPrice(orderBO.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号，即订单号
        orderBO.setId(generateOrderNumber());
        OrderDO orderDO = this.convertFromBoToDo(orderBO);
        orderDOMapper.insertSelective(orderDO);
        //加上商品的销量
        itemService.increaseSales(itemId, amount);
        return orderBO;
    }

    private OrderDO convertFromBoToDo(OrderBO orderBO){
        if(orderBO == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderBO, orderDO);
        return orderDO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNumber(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息,年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        //中间6位为自增序列sequence

        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        //拼接0，凑够6位
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后两位为分库分表位,暂时为定值
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
