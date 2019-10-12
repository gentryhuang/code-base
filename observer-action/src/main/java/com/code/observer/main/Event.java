package com.code.observer.main;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shunhua
 * @date 2019-10-11
 */
@AllArgsConstructor
@Getter
public class Event {

    /**
     * 事件类型
     */
    private final Type type;

    /**
     * Type of change
     */
    public enum Type {
        /**
         * 初始化
         */
        INIT,
        /**
         * 开始
         */
        START,
        /**
         * 结束
         */
        END
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                '}';
    }
}
