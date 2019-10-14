package com.code.rocketmq.spring.boot;

import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * RocketMQ消费者属性
 *
 * @author shunhua
 * @date 2019-10-14
 */
@Data
@ConfigurationProperties(prefix = "spring.rocketmq.consumer")
public class RocketMqConsumerProperties {
    /**
     * 消费者名称
     */
    private String name;
    /**
     * 主题
     */
    private String topic;
    /**
     * 消费组
     */
    private String consumerGroup;
    /**
     * nameServer 的地址
     */
    private String nameServerAddress;

    /**
     * 消费处理器bean的名称
     */
    private String[] consumerHandle;
    /**
     * 消费模式
     */
    private MessageModel messageModel;

    /**
     * 支持配置多个消费者
     */
    private List<RocketMqConsumerProperties> multi;

}
