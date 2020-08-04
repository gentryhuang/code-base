package com.alibaba.study.rpc.framework;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * RpcFramework
 */
public class RpcFramework {
    /**
     * 暴露服务
     *
     * @param service 服务实现
     * @param port    服务端口
     * @throws Exception
     */
    public static void export(final Object service, int port) throws Exception {
        if (service == null) {
            throw new IllegalArgumentException("service instance == null");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port " + port);
        }
        System.out.println("Export service " + service.getClass().getName() + " on port " + port);

        // 以指定端口创建ServerSocket
        ServerSocket server = new ServerSocket(port);

        for (; ; ) {
            try {

                // 等待接收请求
                final Socket socket = server.accept();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {

                                // 获取请求的数据流
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                                try {


                                    // 获取客户端请求的方法名
                                    String methodName = input.readUTF();
                                    // 获取客户端请求的参数类型列表
                                    Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                                    // 获取客户端请求的参数列表
                                    Object[] arguments = (Object[]) input.readObject();

                                    // 创建对象输出流对象，用于响应结果给客户端
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                                    try {

                                        // 通过反射，获取服务接口指定的方法
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                                        // 反射调用
                                        Object result = method.invoke(service, arguments);
                                        // 将结果响应给客户端
                                        output.writeObject(result);

                                    } catch (Throwable t) {
                                        output.writeObject(t);
                                    } finally {
                                        output.close();
                                    }
                                } finally {
                                    input.close();
                                }
                            } finally {
                                socket.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 引用服务
     *
     * @param <T>            接口泛型
     * @param interfaceClass 接口类型
     * @param host           服务器主机名
     * @param port           服务器端口
     * @return 远程服务
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class == null");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        }
        if (host == null || host.length() == 0) {
            throw new IllegalArgumentException("Host == null!");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port " + port);
        }

        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        /**
         * 使用JDK的动态代理创建接口的代理对象
         * 说明：
         * 在 InvocationHandler#invoke方法内部实现Socket与ServerSocket的通信。当使用代理对象调用方法时，内部使用Socket进行通信，然后把通信的结果返回。
         */
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {

                        // 创建Socket，用于连接ServerSocket
                        Socket socket = new Socket(host, port);

                        try {
                            // 创建用于发送数据到ServerSocket的输出流
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                            try {

                                //--------------------- 数据契约 ----------------------------/

                                // 方法名
                                output.writeUTF(method.getName());
                                // 参数类型
                                output.writeObject(method.getParameterTypes());
                                // 参数值
                                output.writeObject(arguments);

                                //------------------------ 数据契约 --------------------------/

                                // 创建用于接收ServerSocket的输入流
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                                try {
                                    // 读取ServerSocket响应的数据
                                    Object result = input.readObject();

                                    if (result instanceof Throwable) {
                                        throw (Throwable) result;
                                    }

                                    // 返回结果
                                    return result;
                                } finally {
                                    input.close();
                                }
                            } finally {
                                output.close();
                            }
                        } finally {
                            socket.close();
                        }
                    }
                });
    }


}
