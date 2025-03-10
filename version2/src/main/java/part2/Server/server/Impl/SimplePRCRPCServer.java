package part2.Server.server.Impl;

import lombok.AllArgsConstructor;
import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author sd
 * @date 2025/3/8 17:13
 * @description: SimpleRPCRPCServer实现RpcServer接口，实现开启监听的方法
 */
@AllArgsConstructor
public class SimplePRCRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println(".........服务器启动了.......");

            while (true){
                //如果没有连接，会堵塞在这里
                Socket accept = serverSocket.accept();
                //有连接，创建一个新的线程执行处理
                new Thread(new WorkThread(accept,serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
