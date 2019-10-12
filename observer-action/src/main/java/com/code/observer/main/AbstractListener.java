package com.code.observer.main;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 抽象监听器
 *
 * @author shunhua
 * @date 2019-10-11
 */
public abstract class AbstractListener implements InitializingBean, Listener {

    @Autowired
    private ObserverService observerService;

    @Override
    public void handleEvent(Event event) {
        if (event.getType() == getEventType()) {
            try {
                actionChange();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Spring启动后,this是子类实现而不是当前抽象父类
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        observerService.addListener(this);
    }

    /**
     * 事件变更，对应的行为发生改变
     */
    protected abstract void actionChange();
}
