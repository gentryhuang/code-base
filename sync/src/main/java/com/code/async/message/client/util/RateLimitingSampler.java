package com.code.async.message.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.Random;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class RateLimitingSampler implements Sampler {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingSampler.class);

    private static final String RATE_KEY = "rate";
    private final Random random = new Random();
    private final Properties properties = new Properties();
    private volatile double rate;
    private File file;

    public RateLimitingSampler() {
        String filename = System.getProperty("user.home") + "/.magiceye/rate.cache";
        File file = null;
        if (!StringUtils.isEmpty(filename)) {
            file = new File(filename);
            if (!file.exists() && file.getParentFile() != null && !file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new IllegalArgumentException("Invalid sample ratefile " + file + ", cause: Failed to create directory " + file.getParentFile() + "!");
                }
            }
        }
        this.file = file;
        loadProperties();
        loadRate();
    }

    private void loadRate() {
        String rate = properties.getProperty(RATE_KEY);
        if (StringUtils.isEmpty(rate) || !NumberUtils.isNumber(rate)) {
            saveRate(1.0d);
        } else {
            this.rate = Double.valueOf(rate.trim());
        }
    }

    private void saveRate(double rate) {
        this.rate = rate;
        properties.setProperty(RATE_KEY, String.valueOf(this.rate));
        doSaveProperties();
    }

    public void setRate(double rate) {
        saveRate(rate);
    }

    private void loadProperties() {
        if (file != null && file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                properties.load(in);
                if (logger.isInfoEnabled()) {
                    logger.info("Load  sample rate file " + file + ", data: " + properties);
                }
            } catch (Throwable e) {
                logger.warn("Failed to sample rate file " + file, e);
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
    }

    private void doSaveProperties() {
        if (file == null) {
            return;
        }
        // 保存
        try {
            File lockfile = new File(file.getAbsolutePath() + ".lock");
            if (!lockfile.exists()) {
                lockfile.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(lockfile, "rw");
            try {
                FileChannel channel = raf.getChannel();
                try {
                    FileLock lock = channel.tryLock();
                    if (lock == null) {
                        throw new IOException("Can not lock the rate cache file " + file.getAbsolutePath() + ", ignore and retry later, maybe multi java process use the file, please config: flame.cursor.file=xxx.properties");
                    }
                    // 保存
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream outputFile = new FileOutputStream(file);
                        try {
                            properties.store(outputFile, "magiceye sample rate");
                        } finally {
                            outputFile.close();
                        }
                    } finally {
                        lock.release();
                    }
                } finally {
                    channel.close();
                }
            } finally {
                raf.close();
            }
        } catch (Throwable e) {
            logger.warn("Failed to save rate file, cause: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isSampled() {
        double offset = random.nextDouble();
        if (offset <= rate) {
            return true;
        }
        return false;
    }
}
