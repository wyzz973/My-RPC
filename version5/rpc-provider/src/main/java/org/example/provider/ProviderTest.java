package org.example.provider;

import org.example.RpcApplication;
import org.example.provider.impl.UserServiceImpl;
import org.example.server.provider.ServiceProvider;
import org.example.server.server.Impl.NettyRPCRPCserver;
import org.example.server.server.RpcServer;
import org.example.service.UserService;

/**
 * @author sd
 * @date 2025/3/8 18:25
 * @description: 服务端
 */
public class ProviderTest {
    public static void main(String[] args) {
        RpcApplication.initialize();
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",9999);

        serviceProvider.provideServiceInterface(userService,true);

//        RpcServer rpcServer = new SimplePRCRPCServer(serviceProvider);
        RpcServer rpcServer = new NettyRPCRPCserver(serviceProvider);

        Runtime.getRuntime().addShutdownHook(new Thread(rpcServer :: stop));

        rpcServer.start(9999);




    }
}
