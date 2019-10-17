package rpc.server;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class RPCServer {

    private static final ExecutorService taskPool = Executors.newFixedThreadPool(50);

    /**
     * 服务接口对象库
     * key：接口名    value：接口实现
     */
    private static final ConcurrentHashMap<String, Object> serviceTargets = new ConcurrentHashMap<>();

    private static AtomicBoolean run = new AtomicBoolean(false);

    /**
     * 注册服务
     *
     * @param service 接口实现类
     */
    public void registerService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces == null) {
            throw new IllegalArgumentException("服务对象必须实现接口");
        }
        Class<?> cls = interfaces[0];
        String interfaceName = cls.getName();
        serviceTargets.put(interfaceName, service);
    }

    /**
     * 启动Server
     *
     * @param port
     */
    public void startServer(final int port) {
        Runnable lifeThread = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket lifeSocket = new ServerSocket(port);
                    run.set(true);
                    while (run.get()) {
                        Socket client = lifeSocket.accept();
                        ServiceTask serviceTask = new ServiceTask(client);
                        serviceTask.accept();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        taskPool.execute(lifeThread);
        System.out.println("服务启动成功...");
    }

    public void stopServer() {
        run.set(false);
        taskPool.shutdown();
    }

    public static final class ServiceTask implements Runnable {

        private Socket client;

        private ServiceTask(Socket client) {
            this.client = client;
        }

        private void accept() {
            taskPool.execute(this);
        }

        @Override
        public void run() {
            InputStream is = null;
            ObjectInput oi = null;
            OutputStream os = null;
            ObjectOutput oo = null;
            try {
                is = client.getInputStream();
                os = client.getOutputStream();
                oi = new ObjectInputStream(is);
                String serviceName = oi.readUTF();
                String methodName = oi.readUTF();
                Class<?>[] paramTypes = (Class[]) oi.readObject();
                Object[] arguments = (Object[]) oi.readObject();
                System.out.println("serviceName:" + serviceName + " methodName:" + methodName);
                Object targetService = serviceTargets.get(serviceName);
                if (targetService == null) {
                    throw new ClassNotFoundException(serviceName + "服务未找到！");
                }

                Method targetMethod = targetService.getClass().getMethod(methodName, paramTypes);
                Object result = targetMethod.invoke(targetService, arguments);

                oo = new ObjectOutputStream(os);
                oo.writeObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oo != null) {
                        oo.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    if (oi != null) {
                        oi.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}