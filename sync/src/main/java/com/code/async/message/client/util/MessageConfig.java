package com.code.async.message.client.util;


import com.code.async.message.client.consumer.ConsumerListenerForRm;
import org.apache.commons.lang3.StringUtils;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class MessageConfig {
    private static String env;

    /**
     * 设置环境
     *
     * @param env
     */
    public void setEnv(String env) {
        MessageConfig.env = env;
    }

    public void setSuspend(boolean suspend) {
        if (checkUnPublishEnv()) {
            ConsumerListenerForRm.suspend = suspend;
        }
    }

    public static boolean checkUnPublishEnv() {
        return StringUtils.containsAny(env, "dev", "daily", "pre");
    }

}
