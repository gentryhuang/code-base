package rpc.test;

import rpc.server.RPCServer;
/**
 *
 * @author shunhua
 * @date 2019-10-18
 */
public class RPCServerTest {

    public static void main(String[] args) {
        RPCServer server = new RPCServer();
        server.registerService(new HelloWorld());
        server.startServer(8080);
    }
}
