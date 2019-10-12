package com.code.exception.handler;

import com.code.common.exception.ExceptionPicker;
import com.code.common.logger.LogFactory;
import com.code.common.logger.LogMarker;
import com.code.common.logger.LoggerUtil;

/**
 * @author shunhua
 * @date 2019-09-26
 */
public class HandDemo {
    public static void main(String[] args) {
        try{
            int a = 1/0;
        }catch (Throwable e){
            handleException(e);
        }
    }

    public static void handleException(Throwable e){
        // 获取异常调用栈信息
        String message = ExceptionPicker.pickup(e);
        LoggerUtil.info(LogFactory.BUSINESS_LOGGER, LogMarker.BUSINESS,message);
    }

}
