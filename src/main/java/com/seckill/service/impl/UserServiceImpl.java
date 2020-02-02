package com.seckill.service.impl;

import com.seckill.dao.UserDOMapper;
import com.seckill.dao.UserPasswordDOMapper;
import com.seckill.dos.UserDO;
import com.seckill.dos.UserPasswordDO;
import com.seckill.service.bos.UserBO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seckill.validator.ValidationResult;
import com.seckill.validator.ValidatorImpl;

/**
 * @author Stephen Jia
 * @create 2020-01-28   11:21
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDOMapper userDOMapper;
    @Autowired
    UserPasswordDOMapper userPasswordDOMapper;
    @Autowired//报错日志，把validator放错包了导致扫描不到
    private ValidatorImpl validator;
    @Override
    public UserBO getUserById(Integer id) {
        //调用userDOMapper获取到对应的dataObject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        //写这一步的时候改造了userPasswordDOMapper接口和映射文件，
        // 因为原来的mapper只有从select by primary key的逻辑，所以增加了一个select By user_id字段的方法
        if(userDO == null){
            return null;
        }
        //通过用户id获取用户对应的加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);
        UserBO userBO = convertFromDataObjectToBusinessObject(userDO, userPasswordDO);
        return userBO;
    }

    @Override
    @Transactional
    public void register(UserBO userBO) throws BusinessException {
        if(userBO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userBO.getName())
//            || userBO.getGender() == null
//            || userBO.getAge() == null
//            || StringUtils.isEmpty(userBO.getTelephone())){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        //改为使用validator完成校验
        ValidationResult result = validator.validate(userBO);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //实现bo转换为do的方法
        UserDO userDO = convertFromBoToDo(userBO);

        //防止重复注册
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "此号码已被注册！");
        }
        //通过在对应mapper的insertSelective标签中设置keyProperty="id" useGeneratedKeys="true"
        //可以将插入时自动生成的自增的id取出，下一步转换时的密码do就可以获取user表的id
        userBO.setId(userDO.getId());
        //实现passwordbo转换为passworddo的方法
        UserPasswordDO userPasswordDO = convertFromBoToPasswordDo(userBO);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;

    }

    @Override
    public UserBO validateLogin(String telephone, String encrptPassword) throws BusinessException {
        //通过用户的手机获取用户信息
        //这里去mapper里面增加一个通过手机号获取用户信息的sql
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        if(userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserBO userBO = convertFromDataObjectToBusinessObject(userDO, userPasswordDO);
        //比对用户信息内部加密的密码是否和传输进来的密码相匹配
        if(!StringUtils.equals(encrptPassword, userBO.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userBO;

    }

    private UserPasswordDO convertFromBoToPasswordDo(UserBO userBO){
        if (userBO == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userBO.getEncrptPassword());
        userPasswordDO.setUserId(userBO.getId());
        return userPasswordDO;
    }


    //实现bo转换为do的方法
    private UserDO convertFromBoToDo(UserBO userBO){
        if(userBO == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userBO, userDO);
        return userDO;
    }

    //getUserById方法返回的是一个业务层应用的的userbo对象，所以这个方法实现对user密码的封装转换
    private UserBO convertFromDataObjectToBusinessObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserBO userBO = new UserBO();
        BeanUtils.copyProperties(userDO, userBO);
        if(userPasswordDO != null){
            userBO.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userBO;
    }
}
