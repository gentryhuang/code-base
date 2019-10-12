package com.code.thread.pool.thread;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * @author shunhua
 * @date 2019-09-25
 */
public enum ThreadPoolManager {
    INSTANCE;

    /**
     * 最少线程数目（当前线程少于此值时，新任务将会被直接创建线程然后放入线程池）
     */
    private int CORE_POOL_SIZE = 60;

    /**
     * 最大允许的线程池数目
     */
    private int MAXI_MUM_POOL_SIZE = 200;
    /**
     * 线程存活时间
     */
    private long KEEP_ALIVE_TIME = 100;
    /**
     * 时间单位：秒
     */
    private TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 阻塞队列
     */
    private int SIZE_WORK_QUEUE = 200;
    /**
     * guava的ExecutorService
     */
    ListeningExecutorService coreExecutorService;


    ThreadPoolManager(){
          final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                  CORE_POOL_SIZE,
                  MAXI_MUM_POOL_SIZE,
                  KEEP_ALIVE_TIME,
                  TIME_UNIT,
                  new ArrayBlockingQueue(SIZE_WORK_QUEUE),
                  new ThreadPoolThreadFactory("business_threadPoll"),
                  new DiscardPolicy()
          );

          /**
           *  1 MoreExecutors.listeningDecorator就是包装了一下ThreadPoolExecutor，返回ListeningExecutorService，目的是为了使用ListenableFuture
           *  2 ListeningExecutorService和ListenableFuture分别对 ThreadPoolExecutor 和 Future的扩展
           */
          coreExecutorService = MoreExecutors.listeningDecorator(threadPool);
    }

    /**
     * 向线程池中添加任务方法
     */
    public <T>ListenableFuture<T> addExecuteTask(Callable<T> task){
        return coreExecutorService.submit(task);
    }

    /**
     * 向线程池中添加任务方法
     */
    public <T> ListenableFuture<T> addCoreExecuteTask(Callable<T> task) {
        return coreExecutorService.submit(task);
    }


}
