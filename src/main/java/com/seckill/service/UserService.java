package com.seckill.service;

import com.seckill.service.bos.UserBO;
import com.seckill.error.BusinessException;

/**
 * @author Stephen Jia
 * @create 2020-01-28   11:21
 */
public interface UserService {
    /**
     * 通过用户id获取User对象的方法
     * @param id user 的 id
     */
    UserBO getUserById(Integer id);

    /**
     * 用户注册的方法
     * @param userBO
     * @throws BusinessException
     */
    void register(UserBO userBO) throws BusinessException;

    UserBO validateLogin(String telephone, String encrptPassword) throws BusinessException;
}
