package com.seckill.response;

import lombok.Data;

/**功能：将controller层的响应归一化处理
 * 以状态码+数据的格式返回给前台(status + object)
 * @author Stephen Jia
 * @create 2020-01-29   9:05
 */
@Data
public class CommonReturnType {
    //表明对应请求的对应返回结果，有success（200,服务器处理正常），fail（其他）
    private String status;
    //若status返回success，则data返回前端需要的json数据
    //若status为fail则data内使用通用的错误码格式,date内包括errCode:..., errMsg:...
    private Object data;

    //定义好一个通用的创建方法,下面两个重载方法的意义是
    //如果传参时不带任何的状态码，那么就是success，并通过重载方法把对应的result封装返回
    //如果传参时就带状态码，那么就会直接调用第二个重载的方法，并且result我们可以设置为错误信息
    public static CommonReturnType create(Object result){
        return create(result, "success");
    }
    public static CommonReturnType create(Object result, String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
}
