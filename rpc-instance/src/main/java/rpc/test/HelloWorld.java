package rpc.test;

/**
 *
 * @author shunhua
 * @date 2019-10-18
 */
public class HelloWorld implements IHelloWorld {

    @Override
    public String sayHello(String name) {
        return "hello " + name + "!";
    }

}
