package part2.Server;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.Impl.NettyRPCRPCserver;
import part2.Server.server.RpcServer;
import part2.common.service.Impl.UserServiceImpl;
import part2.common.service.UserService;

/**
 * @author sd
 * @date 2025/3/8 18:25
 * @description: 服务端
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",9999);

        serviceProvider.provideServiceInterface(userService,true);

//        RpcServer rpcServer = new SimplePRCRPCServer(serviceProvider);
        RpcServer rpcServer = new NettyRPCRPCserver(serviceProvider);

        rpcServer.start(9999);

    }
}
