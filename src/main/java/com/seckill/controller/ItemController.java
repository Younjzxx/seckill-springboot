package com.seckill.controller;

import com.seckill.controller.vos.ItemVO;
import com.seckill.dos.ItemStockDO;
import com.seckill.error.BusinessException;
import com.seckill.response.CommonReturnType;
import com.seckill.service.ItemService;
import com.seckill.service.bos.ItemBO;
import com.seckill.service.bos.KillBO;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stephen Jia
 * @create 2020-01-31   10:24
 */
@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true" ,allowedHeaders = "*")//完成springboot请求跨域
public class ItemController extends BaseController {
    @Autowired
    ItemService itemService;




    //商品列表页面浏览
    @GetMapping(value = "/list")
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemBO> itemBOList = itemService.listItem();
        //使用stream api将list里面的每一个do对象转化为vo
        List<ItemVO> itemVOList = itemBOList.stream().map(itemBO -> {
            ItemVO itemVO = this.convertFromItemBoToItemVo(itemBO);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }





    //创建商品的controller(销量和创建商品无关)
    @PostMapping(value = "/create", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                           @RequestParam(name = "description") String description,
                           @RequestParam(name = "price") BigDecimal price,
                           @RequestParam(name = "stock") Integer stock,
                           @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        //封装service请求用来创建商品
        ItemBO itemBO = new ItemBO();
        itemBO.setTitle(title);
        itemBO.setDescription(description);
        itemBO.setPrice(price);
        itemBO.setStock(stock);
        itemBO.setImgUrl(imgUrl);

        ItemBO itemBoForReturn = itemService.createItem(itemBO);
        ItemVO itemVO = convertFromItemBoToItemVo(itemBoForReturn);
        return CommonReturnType.create(itemVO);
    }

    //商品详情页的浏览，（浏览功能一般用get请求）
    @GetMapping(value = "/get")
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id){
        ItemBO itemBO = itemService.getItemById(id);
        ItemVO itemVO = convertFromItemBoToItemVo(itemBO);
        return CommonReturnType.create(itemVO);
    }


    private ItemVO convertFromItemBoToItemVo(ItemBO itemBO){
        if(itemBO == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemBO, itemVO);
        if(itemBO.getKillBO() != null){
            //有正在进行或者正在进行的秒杀
            itemVO.setKillStatus(itemBO.getKillBO().getStatus());
            itemVO.setKillId(itemBO.getKillBO().getId());
            itemVO.setStartDate(itemBO.getKillBO().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setKillPrice(itemBO.getKillBO().getKillItemPrice());
        }else{
            itemVO.setKillStatus(0);
        }
        return itemVO;


    }
}
