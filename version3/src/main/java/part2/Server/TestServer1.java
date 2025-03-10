package part2.Server;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.Impl.NettyRPCRPCserver;
import part2.Server.server.RpcServer;
import part2.common.service.Impl.UserServiceImpl;
import part2.common.service.UserService;

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
            serviceProvider.provideServiceInterface(userService,true);

            RpcServer rpcServer = new NettyRPCRPCserver(serviceProvider);
            rpcServer.start(port);
        }).start();
    }
}
