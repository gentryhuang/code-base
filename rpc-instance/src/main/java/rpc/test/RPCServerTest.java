package rpc.test;

import rpc.server.RPCServer;

public class RPCServerTest {

    public static void main(String[] args) {
        RPCServer server = new RPCServer();
        server.registerService(new HelloWorld());
        server.startServer(8080);

    }

}
