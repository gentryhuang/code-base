package com.code.cache.jedis.cache;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * @author shunhua
 * @date 2019-09-29
 */
public class RedisService extends RedisBaseService {

    private String ip;

    private int port;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void init() {

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setMaxIdle(this.getMaxIdle());
        config.setMinIdle(this.getMinIdle());
        config.setMaxTotal(this.getMaxTotal());
        config.setMaxWaitMillis(this.getMaxWaitMillis());
        config.setTestOnBorrow(this.isTestOnBorrow());

        pool = new JedisPool(config, ip, port, Protocol.DEFAULT_TIMEOUT, null, database);
    }

    @Override
    public Jedis getResource() {
        return pool.getResource();
    }

    public void destroy() {
        if (pool != null) {
            pool.destroy();
        }
    }
}
