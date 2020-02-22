package com.seckill.controller;

import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.OrderService;
import com.seckill.service.bos.OrderBO;
import com.seckill.service.bos.UserBO;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Stephen Jia
 * @create 2020-02-01   10:51
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true" ,allowedHeaders = "*")//完成springboot请求跨域
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisTemplate redisTemplate;
    //封装下单请求
    @PostMapping(value = "/createorder", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "killId", required = false) Integer killId) throws BusinessException {
        //Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登录，不能下单");
        }
        //获取用户登录信息
        UserBO userBO = (UserBO) redisTemplate.opsForValue().get(token);
        if(userBO == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登录，不能下单");
        }

//        UserBO userBO = (UserBO)httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderBO orderBO = orderService.createOrder(userBO.getId(), itemId, killId, amount);
        return CommonReturnType.create(orderBO);
    }
}
