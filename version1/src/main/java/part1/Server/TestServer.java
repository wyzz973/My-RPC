package part1.Server;

import part1.Server.provider.ServiceProvider;
import part1.Server.server.Impl.SimplePRCRPCServer;
import part1.Server.server.RpcServer;
import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;

/**
 * @author sd
 * @date 2025/3/8 18:25
 * @description: 服务端
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new SimplePRCRPCServer(serviceProvider);

        rpcServer.start(9999);

    }
}
