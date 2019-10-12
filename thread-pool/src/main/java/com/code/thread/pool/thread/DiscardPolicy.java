package com.code.thread.pool.thread;

import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.CommLoggerMarkers;
import com.code.common.logger.LoggerUtil;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 拒绝策略
 * @author shunhua
 * @date 2019-09-25
 */
public class DiscardPolicy implements RejectedExecutionHandler {

    /**
     * 这里拒绝策略进行添加日志进行记录
     * @param r
     * @param executor
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LoggerUtil.error(CommLoggerFactory.THREAD_POOL_LOGGER, CommLoggerMarkers.THREAD_POOL, r.toString(),
                executor.getCompletedTaskCount(),
                executor.getActiveCount(),
                executor.getTaskCount()
        );
    }
}
