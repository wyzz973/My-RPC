package part1.Server;

import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;
import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;
import part1.Server.server.Impl.NettyRPCRPCserver;

public class TestServer1 {
    public static void main(String[] args) {
        startServer("127.0.0.1", 10002);
        startServer("127.0.0.1", 10000);
        startServer("127.0.0.1", 10001);
        startServer("127.0.0.1", 10003);
        startServer("127.0.0.1", 10004);
    }

    public static void startServer(String host, int port) {
        new Thread(() -> {
            UserService userService = new UserServiceImpl();
            ServiceProvider serviceProvider = new ServiceProvider(host, port);
            serviceProvider.provideServiceInterface(userService,true);

            RpcServer rpcServer = new NettyRPCRPCserver(serviceProvider);
            rpcServer.start(port);
        }).start();
    }
}
