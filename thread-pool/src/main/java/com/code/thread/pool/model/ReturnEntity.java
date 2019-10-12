package com.code.thread.pool.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 线程执行结果
 * @author shunhua
 * @date 2019-09-25
 */
public class ReturnEntity {
    /**
     * 执行过程中的异常
     */
    private Exception exception;
    /**
     * 执行结果
     */
    private Object result;

    /**
     * 私有无参构造方法
     */
    private ReturnEntity(){}

    /**
     * 构造默认的返回对象
     * @return
     */
    public static ReturnEntity newDefault(){return new ReturnEntity();}

    /**
     * 获取异常
     * @return
     */
    public Exception getException(){
        return this.exception;
    }

    /**
     * 设置异常
     * @param exception
     */
    public void setException(Exception exception){
        this.exception = exception;
    }

    /**
     * 获取结果
     * @return
     */
    public Object getResult(){return this.result;}

    /**
     * 设置结果
     * @param result
     */
    public void setResult(Object result){
        this.result = result;
    }

    /**
     * 判断是否有结果
     * @return
     */
    public boolean hasResult(){
        return null != this.result;
    }

    /**
     * 抛出异常
     * @throws Exception
     */
    public void throwException() throws Exception {
        if(this.exception != null){
            throw this.exception;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
