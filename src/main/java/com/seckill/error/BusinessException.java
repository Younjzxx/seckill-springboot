package com.seckill.error;

/**
 * @author Stephen Jia
 * @create 2020-01-29   9:32
 */
//包装器业务异常类实现
//BusinessException和EmBusinessError都实现了CommonError中定义的方法，这样
//外部不仅可以new BusinessException 也可以new EmBusinessError 都可以有对应的errCode和errMsg
//并且都实现了setErrMsg的方法，这样BusinessException可以将原来EmBusinessError定义的errMsg覆盖掉
public class BusinessException extends Exception implements CommonError {
    private CommonError commonError;
    //直接接收EmBusinessError的传参用于构造业务异常
    public BusinessException(CommonError commonError){
        super();
        this.commonError = commonError;
    }
    //接收自定义errMsg的方式构造业务异常
    public BusinessException(CommonError commonError, String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }
    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMessage() {
        return this.commonError.getErrorMessage();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
