package rpc.test;

import rpc.client.RPCClient;

/**
 *
 * @author shunhua
 * @date 2019-10-18
 */
public class RPCClientTest {

    public static void main(String[] args) {

        IHelloWorld helloWorld = RPCClient.findService("127.0.0.1", 8080, IHelloWorld.class);
        String result = helloWorld.sayHello("world");
        System.out.println(result);

    }

}