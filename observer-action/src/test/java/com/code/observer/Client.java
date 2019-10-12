package com.code.observer;

import com.code.observer.main.Event;
import com.code.observer.main.ObserverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author shunhua
 * @date 2019-10-11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Client.class)
public class Client {

    @Autowired
    private ObserverService observerService;

    @Test
    public void test(){
        // 触发监听
        observerService.notify(new Event(Event.Type.INIT));
    }
}
