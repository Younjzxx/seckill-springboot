package com.seckill.error;

/**这个枚举类的枚举对象是可以直接通过类名.错误对象调用的
 * @author Stephen Jia
 * @create 2020-01-29   9:22
 */
public enum EmBusinessError implements CommonError {
    //通用错误类型为10001
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    UNKNOWN_ERROR(10002, "未知错误"),
    //规定20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户手机号码或密码不正确"),
    STOCK_NOT_ENOUGH(30001, "库存不足"),//30000开头为交易信息错误
    USER_NOT_LOGIN(20003, "用户还未登录");
    private EmBusinessError(int errorCode, String errMsg){
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }
    private int errorCode;
    private String errMsg;


    @Override
    public int getErrCode() {
        return this.errorCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
