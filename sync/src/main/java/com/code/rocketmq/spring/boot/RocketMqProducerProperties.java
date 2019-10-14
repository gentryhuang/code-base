package com.code.rocketmq.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author shunhua
 * @date 2019-10-14
 */
@Data
@ConfigurationProperties(prefix = "spring.rocketmq.producer")
public class RocketMqProducerProperties {
    /**
     * 消息生产者名
     */
    private String name;
    /**
     * 主题
     */
    private String topic;
    /**
     * 生产者组
     */
    private String producerGroup;
    /**
     * nameServer 的地址
     */
    private String nameServerAddress;

    /**
     * 失败重试次数
     */
    private Integer retryTimesWhenSendFailed;

    /**
     * 最大消息个数
     */
    private Integer maxMessageSize;

    /**
     * 支持配置多个生产者
     */
    private List<RocketMqProducerProperties> multi;

}
