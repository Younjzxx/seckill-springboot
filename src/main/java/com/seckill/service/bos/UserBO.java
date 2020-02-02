package com.seckill.service.bos;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**bo：business object对象：为业务逻辑层model对象
 * model层面用户是包含密码属性的，只是在持久层设计表的时候将其密码放到了另一个表中，
 * 在model层就要将其所有java核心逻辑部分需要的属性全部封装到model层进来
 * @author Stephen Jia
 * @create 2020-01-28   11:28
 */
@Data
public class UserBO {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String name;
    @NotNull(message = "没有填写性别")
    private Byte gender;
    @NotNull(message = "没有填写年龄")
    @Min(value = 0, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    @NotBlank(message = "手机号不能为空")
    private String telephone;
    private String registerMode;
    private String thirdPartyId;
    //不同于do的属性
    @NotBlank(message = "密码不能为空")
    private String encrptPassword;
}
