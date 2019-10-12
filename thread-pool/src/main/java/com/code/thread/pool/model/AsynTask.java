package com.code.thread.pool.model;

import com.code.common.exception.ExceptionPicker;

import java.util.concurrent.Callable;

/**
 * 异步任务类
 * @author shunhua
 * @date 2019-09-25
 */
public class AsynTask implements Callable<ReturnEntity> {

    /**
     * 线程任务名
     */
    private String taskName;
    /**
     * 执行器
     */
    private Execute execute;

    /**
     * 私有有参构造函数
     * @param taskName
     */
    private AsynTask(String taskName){this.taskName = taskName;}

    /**
     * 获取异步任务对象
     * @param taskName
     * @return
     */
    public static AsynTask newTask(String taskName){
        return new AsynTask(taskName);
    }

    public AsynTask registerExecute(Execute execute){
        this.execute = execute;
        return this;
    }

    /**
     * AsynTask实例回调方法，其中执行的是Execute的实现方法
     * @return
     */
    @Override
    public ReturnEntity call(){
        ReturnEntity returnEntity = ReturnEntity.newDefault();
        try {
            /** 函数式编程作为入参 */
            returnEntity.setResult(execute.execute());
        }catch (Exception e){
            returnEntity.setException(new Exception(ExceptionPicker.pickup(e) + taskName,e));
        }
        return returnEntity;
    }

    /**
     * 函数式接口，便于使用函数式编程
     */
    public interface Execute{
        /**
         * 执行方法
         * @return
         * @throws Exception
         */
        Object execute() throws Exception;
    }
}
