package com.code.cache.jedis;

import com.code.cache.jedis.cache.ICacheService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * @author shunhua
 * @date 2019-09-29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CacheApplication.class)
public class BaseTest {
    @Resource
    private ICacheService cacheService;

    @Test
    public void test(){
        if(!ObjectUtils.isEmpty(cacheService)){
            boolean abc = cacheService.exists("abc");
            Assert.assertTrue(!abc);

        }
    }
}
