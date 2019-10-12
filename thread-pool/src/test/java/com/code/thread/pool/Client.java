package com.code.thread.pool;

import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.CommLoggerMarkers;
import com.code.common.logger.LogFactory;
import com.code.common.logger.LoggerUtil;
import com.code.thread.pool.model.AsynTask;
import com.code.thread.pool.model.ReturnEntity;
import com.code.thread.pool.thread.ThreadPoolManager;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author shunhua
 * @date 2019-09-25
 */
@Slf4j
public class Client {

    private ThreadPoolManager threadPoolManager = ThreadPoolManager.INSTANCE;

    @Test
    public void testThreadPool() throws Exception {
        Logger console = LoggerFactory.getLogger("BUSINESS");
        console.info("kjkjklkljkljkl");


       LoggerUtil.error(LogFactory.BUSINESS_LOGGER,new Exception());


      /*  ListenableFuture<ReturnEntity> future1 = threadPoolManager.addExecuteTask(AsynTask.newTask("test_thread_pool")
        .registerExecute(() -> String.valueOf("返回测试线程池结果")));

        ListenableFuture<ReturnEntity> future2 = threadPoolManager.addCoreExecuteTask(AsynTask.newTask("test_futures")
                .registerExecute(() -> null));


        System.out.println("--------------测试ListenableFuture的使用开始--------");
        ReturnEntity returnEntity = future1.get();
        if(returnEntity.hasResult()){
           LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER,CommLoggerMarkers.BUSINESS,"测试线程池");
        }
        System.out.println("--------------测试ListenableFuture的使用结束--------");
        System.out.println();



        System.out.println("--------------测试Futures的使用开始-------------");
        // 合并ListenableFuture
        ListenableFuture<List<ReturnEntity>> allFutures = Futures.successfulAsList(future1,future2);
        List<ReturnEntity> returnEntities = FuturesUtil.handleWithAllReturn(allFutures,1000 * 10 * 2, TimeUnit.MILLISECONDS);
        if(returnEntities.size() > 0){
            returnEntities.stream().forEach(future ->{
                LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER,CommLoggerMarkers.BUSINESS,String.valueOf(future.hasResult()));
            });
        }
        System.out.println("--------------测试Futures的使用结束---------------");*/


    }
}
