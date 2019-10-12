package com.code.thread.pool.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * @author shunhua
 * @date 2019-09-25
 */
public class ThreadPoolThreadFactory implements ThreadFactory {
    /**
     * 线程组
     */
    private final ThreadGroup group;
    /**
     *  线程数
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 线程名称前缀
     */
    private final String namePrefix;

    public ThreadPoolThreadFactory(String poolName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + poolName + "-thread-";
    }

    /**
     * 线程工厂创建线程方法
     * @param r
     * @return
     */
    @Override
    public Thread newThread(Runnable r) {
        // 使用多参数的Thread构造方法
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        // 设置线程优先级
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
