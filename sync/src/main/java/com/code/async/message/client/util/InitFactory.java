package com.code.async.message.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class InitFactory {

    public static final String MAGIC_EYE_PREFIX = "";
    public static final String SERVICE_NAME_KEY = MAGIC_EYE_PREFIX + "APP_NAME";
    public static final String BUFFER_MAX_SIZE_KEY = MAGIC_EYE_PREFIX + "BUFFER_MAX_SIZE";
    public static final String FLUSH_PERIOD = MAGIC_EYE_PREFIX + "FLUSH_PERIOD";
    public static final String KAFKA_SERVER_KEY = MAGIC_EYE_PREFIX + "KAFKA_ADDR";
    public static final String SPAN_MAX_SIZE_KEY = MAGIC_EYE_PREFIX + "SPAN_MAX_SIZE";
    public static final String DEFAULT_SERVICE_NAME = "default";
    public static final Reporter DEFAULT_REPORTER = new NoopReporter();
    public static final Integer DEFAULT_MAX_SIZE = 1024 * 8;
    public static final Long DEFAULT_FLUSH_PERIOD = 5000L;
    private static final Logger logger = LoggerFactory.getLogger(InitFactory.class);

    public static String getServiceName() {
        String serviceName = PropertyUtils.getProperty(SERVICE_NAME_KEY);
        if (!StringUtils.isEmpty(serviceName)) {
            return serviceName;
        }
        File file = new File("/opt/app/app.info");
        if (!file.exists() && file.getParentFile() != null && !file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                logger.warn("Invalid serviceName file {}, cause: Failed to create directory {} !", file, file.getParentFile());
                return DEFAULT_SERVICE_NAME;
            }
        }
        if (file != null && file.exists()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                logger.info("Load  serviceName file " + file);
                return in.readLine();
            } catch (Throwable e) {
                logger.warn("Failed to serviceName file " + file, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        }
        return DEFAULT_SERVICE_NAME;
    }

    public static Sampler getSampler() {
        return new RateLimitingSampler();
    }

    public static Reporter getReporter() {
        Sender sender = getSender();
        if (sender == null) {
            return DEFAULT_REPORTER;
        }
        try {
            return new DisruptorReporter(
                    PropertyUtils.numberOrDefault(
                            PropertyUtils.getPropertyAsInt(BUFFER_MAX_SIZE_KEY), DEFAULT_MAX_SIZE).intValue(),
                    PropertyUtils.numberOrDefault(
                            PropertyUtils.getPropertyAsLong(FLUSH_PERIOD), DEFAULT_FLUSH_PERIOD).longValue(),
                    sender);
        } catch (Throwable e) {
            logger.error("Disruptor init fail", e);
        }
        return DEFAULT_REPORTER;
    }

    private static Sender getSender() {
        String kafkaServerAddress = PropertyUtils.getProperty(KAFKA_SERVER_KEY);
        if (StringUtils.isEmpty(kafkaServerAddress)) {
            logger.error("kafkaServerAddress is null");
            return null;
        }
        return null;
    }
}
