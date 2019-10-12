package com.code.observer.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用层
 *
 * @author shunhua
 * @date 2019-10-11
 */
@RestController
public class ClientController {

    @Autowired
    private ObserverService observerService;

    @GetMapping("/get")
    public String hello() {

        observerService.notify(new Event(Event.Type.INIT));
        observerService.notify(new Event(Event.Type.START));
        observerService.notify(new Event(Event.Type.END));
        return "hello world";
    }
}
