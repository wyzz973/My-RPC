package part1.Server;

import part1.Server.provider.ServiceProvider;
import part1.Server.server.Impl.NettyRPCRPCserver;
import part1.Server.server.RpcServer;
import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;

public class TestServer1 {
    public static void main(String[] args) {
        startServer("127.0.0.1", 9999);
        startServer("127.0.0.1", 10000);
        startServer("127.0.0.1", 10001);
    }

    public static void startServer(String host, int port) {
        new Thread(() -> {
            UserService userService = new UserServiceImpl();
            ServiceProvider serviceProvider = new ServiceProvider(host, port);
            serviceProvider.provideServiceInterface(userService);

            RpcServer rpcServer = new NettyRPCRPCserver(serviceProvider);
            rpcServer.start(port);
        }).start();
    }
}
