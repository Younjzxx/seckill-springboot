package com.seckill.error;

/**
 * @author Stephen Jia
 * @create 2020-01-29   9:20
 */
public interface CommonError {
    public int getErrorCode();
    public String getErrorMessage();
    public CommonError setErrMsg(String errMsg);
}
