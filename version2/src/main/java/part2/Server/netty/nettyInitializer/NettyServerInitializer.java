package part2.Server.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import part2.Server.netty.handler.NettyServerHandler;
import part2.Server.provider.ServiceProvider;
import part2.common.serializer.myCode.MyDecoder;
import part2.common.serializer.myCode.MyEncoder;
import part2.common.serializer.mySerializer.JsonSerializer;


/**
 * @author sd
 * @date 2025/3/9 21:01
 * @description: Netty 服务初始化
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        /**
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4)
        );

        pipeline.addLast(new LengthFieldPrepender(4));

        pipeline.addLast(new ObjectEncoder());

        pipeline.addLast(new ObjectDecoder(Class :: forName));

       */
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());

        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
