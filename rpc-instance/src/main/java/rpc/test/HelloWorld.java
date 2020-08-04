package rpc.test;

/**
 * @author shunhua
 * @date 2019-10-18
 */
public class HelloWorld implements IHelloWorld {

    /**
     * 服务接口实现
     *
     * @param name
     * @return
     */
    @Override
    public String sayHello(String name) {
        return "hello " + name + "!";
    }

}
