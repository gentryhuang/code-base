package com.code.async.message.client.constants;

/**
 * 延迟消息枚举
 * <p>
 * messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
 * 级别 =       1  2  3   4   5  6  7  8  9  10 11 12 13  14  15  16 17 18
 *
 * @author shunhua
 * @date 2019-10-14
 */
public enum NotifyDelayType {
    LEVEL_1(1, 1),
    LEVEL_2(2, 5),
    LEVEL_3(3, 10),
    LEVEL_4(4, 30),
    LEVEL_5(5, 60),
    LEVEL_6(6, 120),
    LEVEL_7(7, 180),
    LEVEL_8(8, 240),
    LEVEL_9(9, 300),
    LEVEL_10(10, 360),
    LEVEL_11(11, 420),
    LEVEL_12(12, 480),
    LEVEL_13(13, 540),
    LEVEL_14(14, 600),
    LEVEL_15(15, 1200),
    LEVEL_16(16, 1800),
    LEVEL_17(17, 3600),
    LEVEL_18(18, 7200),
    ;

    /**
     * 延迟级别
     */
    private int level;
    /**
     * 延迟秒
     */
    private int delaySecond;

    NotifyDelayType(int level, int delaySecond) {
        this.level = level;
        this.delaySecond = delaySecond;
    }


    public int getLevel() {
        return level;
    }


    public void setLevel(int level) {
        this.level = level;
    }


    public int getDelaySecond() {
        return delaySecond;
    }


    public void setDelaySecond(int delaySecond) {
        this.delaySecond = delaySecond;
    }
}
