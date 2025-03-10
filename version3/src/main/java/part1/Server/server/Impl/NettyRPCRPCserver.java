package part1.Server.server.Impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import part1.Server.netty.nettyInitializer.NettyServerInitializer;
import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;

/**
 * @author sd
 * @date 2025/3/9 20:57
 * @description: NettyRPCRPCServer类实现RpcServer接口
 */
@AllArgsConstructor
public class NettyRPCRPCserver implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        // netty 服务线程组boss负责建立连接， work负责具体的请求

        /**
         * bossGroup：负责监听和接受客户端连接，就像大门口的接待员，接收所有进来的连接请求。
         * workGroup：负责处理已建立连接的 I/O 操作和业务逻辑，就像分配到不同服务员处理具体服务请求。
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        System.out.println("netty服务端启动了");


        /**
         * ServerBootstrap：Netty 用于配置和启动服务器的类。
         * group(bossGroup, workGroup)：为服务器分配两个线程组，一个负责连接（boss），另一个负责 I/O 操作（worker）。
         * channel(NioServerSocketChannel.class)：指定使用 NIO 的非阻塞服务器套接字通道。
         * childHandler(new NettyServerInitializer(serviceProvider))：设置每个新建立的连接所使用的 ChannelPipeline。
         * NettyServerInitializer 会配置解码器、编码器以及业务处理器，并将 serviceProvider 传入，以便后续根据客户端请求找到相应的服务实例。
         */
        try {
            //启动netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //初始化
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    //NettyClientInitializer这里 配置netty对消息的处理机制
                    .childHandler(new NettyServerInitializer(serviceProvider));
            //同步堵塞
            /**
             * bind(port)：服务器绑定到指定端口，开始监听客户端连接。
             * sync()：阻塞等待绑定操作完成，确保服务器成功启动。
             */
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //死循环监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

    @Override
    public void stop() {

    }
}
