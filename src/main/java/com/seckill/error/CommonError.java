package com.seckill.error;

/**
 * @author Stephen Jia
 * @create 2020-01-29   9:20
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
