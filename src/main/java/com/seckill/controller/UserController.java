package com.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.seckill.service.bos.UserBO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.impl.UserServiceImpl;
import com.seckill.controller.vos.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author Stephen Jia
 * @create 2020-01-28   10:57
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true" ,allowedHeaders = "*")//完成springboot请求跨域
public class UserController extends BaseController{
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HttpServletRequest httpServletRequest;



    //用户登录的接口
    @PostMapping(value = "/login", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //参数校验，非空
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录服务,用来校验用户登录是否合法
        UserBO userBO = userService.validateLogin(telephone, this.EncodeByMd5(password));
        //将登录凭证加入到用户登陆成功的session
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userBO);
        return CommonReturnType.create(null);

    }

    //用户注册接口,从前端获取的参数包括电话，验证码和uservo的全部属性
    @PostMapping(value = "/register", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Byte gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证用户的手机号和姓名是否符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);
        if(!StringUtils.equals(otpCode, inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        //用户合法，进入用户注册流程
        UserBO userBO = new UserBO();
        userBO.setName(name);
        userBO.setAge(age);
        userBO.setGender(new Byte(String.valueOf(gender.intValue())));
        userBO.setTelephone(telephone);
        userBO.setRegisterMode("byphone");
        userBO.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userBO);
        return CommonReturnType.create(null);
    }


    //密码加密
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }


    //用户获取otp短信的接口
    @PostMapping(value = "/getotp", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone){
        //需要按照一定的规则生成otp验证码
        Random random = new Random();
        //[0, 900000)随机数
        int randomInt = random.nextInt(900000);
        randomInt += 100000; //[100000, 1000000)
        String otpCode = String.valueOf(randomInt);

        //将otp验证码同对应用户的手机号关联,使用httpsession的方式绑定用户的手机号和otpCode
        httpServletRequest.getSession().setAttribute(telephone, otpCode);//用户的otpCode被用户手机号关联

        //将otp验证码通过短信通道发送给用户，省略,并替换为打印到控制台的方式
        System.out.println("telephone = " + telephone + " & otpCode = " + otpCode);//telephone = 13833333333 & otpCode = 47733
        return CommonReturnType.create(null);
    }



    @RequestMapping("/get")
    @ResponseBody //返回的是json数据而不是地址
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        //RequestParam获取页面上用户操作从而传过来的id，然后
        //调用service服务获取对应id的用户对象并返回给前端回显
        //但是如下两行代码将加密后密码也返回给了前端，非常危险
        //所以为了这个问题加一了层专门为了视图层而创建的模型对象层viewobject(vo)
        UserBO userBO = userService.getUserById(id);
        System.out.println(userBO);
        if(userBO == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //将返回的类型从userModel转换为了userVO，即将核心领域模型对象转化为可供ui使用的viewobject
        UserVO userVO = convertFromBOToVO(userBO);
        //返回通用对象
        return CommonReturnType.create(userVO);
    }


    private UserVO convertFromBOToVO(UserBO userBO){
        if(userBO == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userBO, userVO);
        return userVO;
    }
}




