package rpc.test;


public class HelloWorld implements IHelloWorld {

    @Override
    public String sayHello(String name) {
        return "hello " + name + "!";
    }

}
