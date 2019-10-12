package com.code.cache.jedis.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shunhua
 * @date 2019-09-29
 */
@ConfigurationProperties(prefix = "spring.redis")
public class RedisServiceProperties {

    private List<MultiRedisProperties> multi = new ArrayList<MultiRedisProperties>();

    private MultiRedisProperties redis = new MultiRedisProperties();

    public MultiRedisProperties getRedis() {
        return redis;
    }

    public void setRedis(MultiRedisProperties redis) {
        this.redis = redis;
    }

    public String getName() {
        return redis.getName();
    }

    public void setName(String name) {
        redis.setName(name);
    }

    public int getDatabase() {
        return redis.getDatabase();
    }

    public void setDatabase(int database) {
        redis.setDatabase(database);
    }

    public String getHost() {
        return redis.getHost();
    }

    public void setHost(String host) {
        redis.setHost(host);
    }

    public String getPassword() {
        return redis.getPassword();
    }

    public void setPassword(String password) {
        redis.setPassword(password);
    }

    public int getPort() {
        return redis.getPort();
    }

    public void setPort(int port) {
        redis.setPort(port);
    }

    public void setTimeout(int timeout) {
        redis.setTimeout(timeout);
    }

    public int getTimeout() {
        return redis.getTimeout();
    }

    public RedisProperties.Sentinel getSentinel() {
        return redis.getSentinel();
    }

    public void setSentinel(RedisProperties.Sentinel sentinel) {
        redis.setSentinel(sentinel);
    }

    public MultiRedisProperties.Pool getPool() {
        return redis.getPool();
    }

    public void setPool(MultiRedisProperties.Pool pool) {
        redis.setPool(pool);
    }

    public RedisProperties.Cluster getCluster() {
        return redis.getCluster();
    }

    public void setCluster(RedisProperties.Cluster cluster) {
        redis.setCluster(cluster);
    }

    public List<MultiRedisProperties> getMulti() {
        return multi;
    }

    public void setMulti(List<MultiRedisProperties> multi) {
        this.multi = multi;
    }

    public static class MultiRedisProperties{

        private String name;

        protected int database = 0;

        private MultiRedisProperties.Pool pool;

        public String getHost() {
            return redisProperties.getHost();
        }

        public void setHost(String host) {
            redisProperties.setHost(host);
        }

        public String getPassword() {
            return redisProperties.getPassword();
        }

        public void setPassword(String password) {
            redisProperties.setPassword(password);
        }

        public int getPort() {
            return redisProperties.getPort();
        }

        public void setPort(int port) {
            redisProperties.setPort(port);
        }

        public void setTimeout(int timeout) {
            redisProperties.setTimeout(timeout);
        }

        public int getTimeout() {
            return redisProperties.getTimeout();
        }

        public RedisProperties.Sentinel getSentinel() {
            return redisProperties.getSentinel();
        }

        public void setSentinel(RedisProperties.Sentinel sentinel) {
            redisProperties.setSentinel(sentinel);
        }

        public MultiRedisProperties.Pool getPool() {
            return pool;
        }

        public void setPool(MultiRedisProperties.Pool pool) {
            this.pool = pool;
        }

        public RedisProperties.Cluster getCluster() {
            return redisProperties.getCluster();
        }

        public void setCluster(RedisProperties.Cluster cluster) {
            redisProperties.setCluster(cluster);
        }

        private RedisProperties redisProperties = new RedisProperties();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }


        public static class Pool extends RedisProperties.Pool {
            //最大redis连接池数量
            protected Integer maxTotal = 200;

            private Long timeBetweenEvictionRunsMillis = 60000L;

            private Boolean testOnBorrow = true;

            public Integer getMaxTotal() {
                return maxTotal;
            }

            public void setMaxTotal(Integer maxTotal) {
                this.maxTotal = maxTotal;
            }

            public Long getTimeBetweenEvictionRunsMillis() {
                return timeBetweenEvictionRunsMillis;
            }

            public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
                this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
            }

            public Boolean getTestOnBorrow() {
                return testOnBorrow;
            }

            public void setTestOnBorrow(Boolean testOnBorrow) {
                this.testOnBorrow = testOnBorrow;
            }
        }
    }

}
