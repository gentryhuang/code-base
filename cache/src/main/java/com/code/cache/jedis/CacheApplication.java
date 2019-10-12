package com.code.cache.jedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author shunhua
 * @date 2019-09-29
 */
@SpringBootApplication(scanBasePackages = {"com.code.cache.jedis.cache"})
public class CacheApplication {
    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CacheApplication.class);
        springApplication.setWebEnvironment(false);
        applicationContext = springApplication.run(args);
    }
    public static void exit() {
        SpringApplication.exit(applicationContext);
    }
}
