package com.code.observer.main;

import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.CommLoggerMarkers;
import com.code.common.logger.LoggerUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略包装类
 *
 * @author shunhua
 * @date 2019-10-11
 */
@Service
public class ObserverService {

    /**
     * 类型与对应的监听器
     */
    private final Map<Event.Type, List<Listener>> listeners = new HashMap<>();


    /**
     * 监听事件- 增加事件类型与对应的监听器
     *
     * @param listener 监听器（有对应的事件类型以及处理方法）
     */
    public void addListener(final Listener listener) {
        synchronized (listeners) {
            if (!listeners.containsKey(listener.getEventType())) {
                listeners.put(listener.getEventType(), new ArrayList<>());
            }
            listeners.get(listener.getEventType()).add(listener);
        }
    }

    /**
     * 触发事件 - 应用层指定事件，对应的监听器会捕捉到，然后执行处理方法
     *
     * @param event
     */
    public void notify(final Event event) {
        if (listeners.containsKey(event.getType())) {
            listeners.get(event.getType()).forEach(listener -> listener.handleEvent(event));
        } else {
            LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, this.getClass().getSimpleName());
        }
    }
}
