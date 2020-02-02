package com.seckill.validator;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stephen Jia
 * @create 2020-01-30   15:41
 */
@Data
public class ValidationResult {
    //校验结果是否有错
    private boolean hasErrors = false;

    //存放错误信息的map
    private Map<String, String> errMsgMap = new HashMap<>();



    //实现公用的通过格式化字符串信息，获取错误信息的message方法
    public String getErrMsg(){
       return StringUtils.join(errMsgMap.values().toArray(), ",");
    }


}
